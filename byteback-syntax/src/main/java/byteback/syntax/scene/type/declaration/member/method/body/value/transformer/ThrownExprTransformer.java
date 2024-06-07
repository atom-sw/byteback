package byteback.syntax.scene.type.declaration.member.method.body.value.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.ThrownRef;
import soot.*;
import soot.jimple.InvokeExpr;

/**
 * @author paganma
 */
public class ThrownExprTransformer extends ValueTransformer {

	private static final Lazy<ThrownExprTransformer> INSTANCE = Lazy.from(ThrownExprTransformer::new);

	public static ThrownExprTransformer v() {
		return INSTANCE.get();
	}

	private ThrownExprTransformer() {
	}

	@Override
	public void transformValue(final SootMethod sootMethod, final Body body, final ValueBox valueBox) {
		final Value value = valueBox.getValue();

		if (value instanceof final InvokeExpr invokeExpr) {
			final SootMethodRef invokedMethodRef = invokeExpr.getMethodRef();

			if (BBLibNames.v().isSpecialClass(invokedMethodRef.getDeclaringClass())) {
				if (invokedMethodRef.getName().equals(BBLibNames.THROWN_NAME)) {
					final ThrownRef thrownRef = Vimp.v().newThrownRef();
					valueBox.setValue(thrownRef);
				}
			}
		}
	}

}
