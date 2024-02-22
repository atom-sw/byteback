package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.SootBodies;
import byteback.util.Cons;
import java.util.List;
import java.util.Map;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.grimp.GrimpBody;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;
import soot.util.HashChain;

public class ExpressionFolder extends BodyTransformer {

	public ExpressionFolder() {
	}

	@Override
	protected void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
		if (body instanceof GrimpBody) {
			transformBody(body);
		} else {
			throw new IllegalArgumentException("Can only transform Grimp");
		}
	}

	public boolean sideCondition(final Value substitution) {
		return true;
	}

	public void transformBody(final Body body) {
		final BlockGraph graph = SootBodies.makeBlockGraph(body);
		final SimpleLocalDefs localDefs = new SimpleLocalDefs(SootBodies.makeUnitGraph(body));
		final SimpleLocalUses localUses = new SimpleLocalUses(body, localDefs);

		for (final Block block : graph) {
			final var substitutionTracker = new SubstitutionTracker();
			final var unitsSnapshot = new HashChain<Unit>();

			for (final Unit unit : block) {
				unitsSnapshot.add(unit);
			}

			for (final Unit unit : unitsSnapshot) {
				substitutionTracker.track(unit);

				FOLD_NEXT:
				for (final ValueBox valueBox : unit.getUseBoxes()) {
					final Value value = valueBox.getValue();

					if (value instanceof final Local local) {
						final Cons<Unit, Value> substitutionPair = substitutionTracker.substitute(local);

						if (substitutionPair != null && !substitutionPair.car.equals(unit)) {
							final Unit definition = substitutionPair.car;
							final Value substitution = substitutionPair.cdr;

							if (localDefs.getDefsOfAt(local, unit).size() > 1 && sideCondition(substitution)) {
								continue;
							} else {
								final List<UnitValueBoxPair> usePairs = localUses.getUsesOf(definition);

								for (final UnitValueBoxPair usePair : usePairs) {
									final Unit useUnit = usePair.getUnit();

									if (!unitsSnapshot.contains(useUnit)) {
										continue FOLD_NEXT;
									}
								}
							}

							valueBox.setValue(substitution);
							block.remove(definition);
						}
					}
				}
			}
		}
	}

}
