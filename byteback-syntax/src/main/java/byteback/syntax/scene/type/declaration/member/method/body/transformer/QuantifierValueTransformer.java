package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.QuantifierExpr;
import byteback.syntax.transformer.TransformationException;
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.util.HashChain;

import java.util.Iterator;
import java.util.List;

/**
 * Transforms BBLib's quantifier expressions.
 *
 * @author paganma
 */
public class QuantifierValueTransformer extends BodyTransformer {

	private static final Lazy<QuantifierValueTransformer> INSTANCE = Lazy.from(QuantifierValueTransformer::new);

	private QuantifierValueTransformer() {
	}

	public static QuantifierValueTransformer v() {
		return INSTANCE.get();
	}

	@Override
	public void transformBody(final Body body) {
		final PatchingChain<Unit> units = body.getUnits();
		final Iterator<Unit> unitIterator = units.snapshotIterator();
		final var unitGraph = new BriefUnitGraph(body);
		final var localDefs = new SimpleLocalDefs(unitGraph);

		while (unitIterator.hasNext()) {
			final Unit unit = unitIterator.next();

			if (unit instanceof final AssignStmt assignStmt) {
				final Value rightOp = assignStmt.getRightOp();

				if (rightOp instanceof final InvokeExpr invokeExpr) {
					final SootMethodRef invokedMethodRef = invokeExpr.getMethodRef();
					final SootClass declaringClass = invokedMethodRef.getDeclaringClass();

					if (BBLibNames.v().isBindingsClass(declaringClass)) {
						body.getUnits().remove(assignStmt);
						continue;
					}
				}
			}

			for (final ValueBox valueBox : unit.getUseBoxes()) {
				final Value value = valueBox.getValue();

				if (value instanceof final StaticInvokeExpr invokeExpr) {
					final SootMethod invokedMethod = invokeExpr.getMethod();
					final SootClass declaringClass = invokedMethod.getDeclaringClass();

					if (BBLibNames.v().isQuantifiersClass(declaringClass)) {
						assert invokeExpr.getArgCount() == 2;
						final var locals = new HashChain<Local>();
						final Value bindingValue = invokeExpr.getArg(0);
						final Value condition = invokeExpr.getArg(1);

						if (bindingValue instanceof final Local bindingLocal) {
							locals.add(bindingLocal);
							final List<Unit> defUnits = localDefs.getDefsOfAt(bindingLocal, unit);

							for (final Unit defUnit : defUnits) {
								units.remove(defUnit);
							}
						} else {
							throw new TransformationException(
									"First argument of quantifier method must be a local variable, got: "
											+ bindingValue + ".",
									unit);
						}

						final QuantifierExpr substitute = switch (invokedMethod.getName()) {
							case BBLibNames.UNIVERSAL_QUANTIFIER_NAME ->
								Vimp.v().newForallExpr(locals, condition);
							case BBLibNames.EXISTENTIAL_QUANTIFIER_NAME ->
								Vimp.v().newExistsExpr(locals, condition);
							default -> throw new IllegalStateException(
									"Unknown quantifier method "
											+ invokedMethod.getName());
						};

						valueBox.setValue(substitute);
					}
				}
			}
		}
	}

}
