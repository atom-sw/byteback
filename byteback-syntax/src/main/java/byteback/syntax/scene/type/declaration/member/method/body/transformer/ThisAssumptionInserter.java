package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import soot.*;
import soot.jimple.Jimple;
import soot.jimple.NullConstant;

/**
 * Explicitly introduces the basic assumption that @this != null.
 *
 * @author paganma
 */
public class ThisAssumptionInserter extends BodyTransformer {

	private static final Lazy<ThisAssumptionInserter> INSTANCE = Lazy.from(ThisAssumptionInserter::new);

	public static ThisAssumptionInserter v() {
		return INSTANCE.get();
	}

	private ThisAssumptionInserter() {
	}

	@Override
	public void transformBody(final SootMethod sootMethod, final Body body) {
		if (BehaviorTagMarker.v().hasTag(sootMethod) || sootMethod.isStatic()) {
			return;
		}

		final SootClass sootClass = sootMethod.getDeclaringClass();
		final RefType declaringType = sootClass.getType();
		final PatchingChain<Unit> units = body.getUnits();
		final Immediate condition = Vimp.v().nest(
				Jimple.v().newNeExpr(
						Vimp.v().nest(
								Jimple.v().newThisRef(declaringType)),
						NullConstant.v()));
		final Unit assumeUnit = Vimp.v().newAssumeStmt(condition);
		units.addFirst(assumeUnit);
	}

}
