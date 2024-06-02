package byteback.syntax.scene.type.declaration.member.method.transformer;

import java.util.List;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.context.MethodContext;
import byteback.syntax.scene.type.declaration.member.method.tag.InstanceChecksTag;
import byteback.syntax.scene.type.declaration.member.method.tag.InstanceChecksTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTagAccessor;
import soot.RefType;
import soot.SootMethod;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InstanceOfExpr;

public class InstanceChecksTagger extends MethodTransformer {

	private static final Lazy<InstanceChecksTagger> INSTANCE = Lazy.from(InstanceChecksTagger::new);

	public static InstanceChecksTagger v() {
		return INSTANCE.get();
	}

	private void tagValue(final SootMethod sootMethod, final ValueBox valueBox) {
		final Value value = valueBox.getValue();

		if (value instanceof final InstanceOfExpr instanceOfExpr) {
			InstanceChecksTagAccessor.v().putIfAbsent(sootMethod, InstanceChecksTag::new)
				.addCheckedTypes((RefType) instanceOfExpr.getCheckType());
		}
	}

	private void tagValues(final SootMethod sootMethod, final List<ValueBox> valueBoxes) {
		for (final ValueBox valueBox : valueBoxes) {
			tagValue(sootMethod, valueBox);
		}
	}

	public void transformMethod(final MethodContext methodContext) {
		final SootMethod sootMethod = methodContext.getSootMethod();

		PreconditionsTagAccessor.v().get(sootMethod)
			.ifPresent((preconditionsTag) -> tagValues(sootMethod, preconditionsTag.getUseBoxes()));

		PostconditionsTagAccessor.v().get(sootMethod)
			.ifPresent((postconditionsTag) -> tagValues(sootMethod, postconditionsTag.getUseBoxes()));

		if (sootMethod.hasActiveBody()) {
			tagValues(sootMethod, sootMethod.getActiveBody().getUseBoxes());
		}
	}

}
