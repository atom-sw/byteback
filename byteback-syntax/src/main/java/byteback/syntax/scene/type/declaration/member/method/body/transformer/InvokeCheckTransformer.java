package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.ThrownRef;
import byteback.syntax.scene.type.declaration.member.method.body.value.UnitConstant;
import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpEffectEvaluator;
import soot.*;
import soot.jimple.Jimple;

import java.util.Iterator;

/**
 * Introduces explicit guards checking if the method that was just invoked threw
 * an exception. The effect introduced by this transformation is: if after
 * invoking a method @caughtexception is not @void, @caughtexception must be
 * thrown again, otherwise execution can resume as normal.
 *
 * @author paganma
 */
public class InvokeCheckTransformer extends BodyTransformer {

	private static final Lazy<InvokeCheckTransformer> INSTANCE = Lazy.from(InvokeCheckTransformer::new);

	private InvokeCheckTransformer() {
	}

	public static InvokeCheckTransformer v() {
		return INSTANCE.get();
	}

	@Override
	public void transformBody(final BodyContext bodyContext) {
		final Body body = bodyContext.getBody();
		final PatchingChain<Unit> units = body.getUnits();
		final Iterator<Unit> unitIterator = units.snapshotIterator();

		while (unitIterator.hasNext()) {
			final Unit unit = unitIterator.next();

			for (final ValueBox valueBox : unit.getUseBoxes()) {
				final Value value = valueBox.getValue();

				if (VimpEffectEvaluator.v().isStatefulInvoke(value)) {
					final ThrownRef thrownRef = Vimp.v().newThrownRef();
					final Unit thenBranch = Jimple.v().newThrowStmt(
							Vimp.v().nest(thrownRef));
					units.insertAfter(thenBranch, unit);
					final Unit elseBranch = units.getSuccOf(thenBranch);
					final Value condition = Jimple.v().newEqExpr(
							Vimp.v().nest(thrownRef),
							UnitConstant.v());
					final Unit ifUnit = Jimple.v().newIfStmt(condition, elseBranch);
					units.insertAfter(ifUnit, unit);
				}
			}
		}
	}

}
