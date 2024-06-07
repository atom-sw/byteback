package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.UnitConstant;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import soot.*;
import soot.jimple.Jimple;

/**
 * Explicitly introduces the basic assumption that @thrown != unit at the start
 * of the method.
 *
 * @author paganma
 */
public class ThrownAssumptionInserter extends BodyTransformer {

	private static final Lazy<ThrownAssumptionInserter> INSTANCE = Lazy.from(ThrownAssumptionInserter::new);

	public static ThrownAssumptionInserter v() {
		return INSTANCE.get();
	}

	private ThrownAssumptionInserter() {
	}

	@Override
	public void transformBody(final SootMethod sootMethod, final Body body) {
		if (BehaviorTagMarker.v().hasTag(sootMethod)) {
			return;
		}

		final PatchingChain<Unit> units = body.getUnits();
		final Value condition = Jimple.v().newEqExpr(
				Vimp.v().nest(
						Vimp.v().newThrownRef()),
				UnitConstant.v());
		final Unit assumeUnit = Vimp.v().newAssumeStmt(Vimp.v().nest(condition));
		units.addFirst(assumeUnit);
	}

}
