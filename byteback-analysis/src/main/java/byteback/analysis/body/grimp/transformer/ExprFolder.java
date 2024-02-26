package byteback.analysis.body.grimp.transformer;

import byteback.analysis.body.common.SootBodies;
import byteback.common.Cons;
import soot.*;
import soot.jimple.AssignStmt;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;
import soot.util.HashChain;

import java.util.List;
import java.util.Map;

public class ExprFolder extends BodyTransformer {

    @Override
    protected void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        transformBody(body);
    }

    public boolean canSubstitute(final Value substitution) {
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
                        final Cons<AssignStmt, Value> substitutionPair = substitutionTracker.substitute(local);

                        if (substitutionPair != null && !substitutionPair.car.equals(unit)) {
                            final AssignStmt definition = substitutionPair.car;
                            final Value substitution = substitutionPair.cdr;

                            if (localDefs.getDefsOfAt(local, unit).size() > 1 && canSubstitute(substitution)) {
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

                            valueBox.setValue(value);
                            block.remove(definition);
                        }
                    }
                }
            }
        }
    }

}
