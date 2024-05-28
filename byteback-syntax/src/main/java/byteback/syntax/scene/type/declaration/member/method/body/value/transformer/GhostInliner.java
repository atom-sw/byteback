package byteback.syntax.scene.type.declaration.member.method.body.value.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.body.value.context.ValueContext;
import soot.SootMethodRef;
import soot.Value;
import soot.ValueBox;
import soot.jimple.StaticInvokeExpr;

/**
 * Transforms invocations to behavioral functions to pure (mathematical)
 * function calls.
 *
 * @author paganma
 */
public class GhostInliner extends ValueTransformer {

	private static final Lazy<GhostInliner> INSTANCE = Lazy.from(GhostInliner::new);

	public static GhostInliner v() {
		return INSTANCE.get();
	}

	private GhostInliner() {
	}

	@Override
	public void transformValue(final ValueContext valueContext) {
		final ValueBox valueBox = valueContext.getValueBox();
		final Value value = valueBox.getValue();

		if (value instanceof final StaticInvokeExpr staticInvokeExpr) {
			final SootMethodRef invokedMethodRef = staticInvokeExpr.getMethodRef();

			if (BBLibNames.v().isGhostClass(invokedMethodRef.getDeclaringClass())
					&& invokedMethodRef.getName().equals(BBLibNames.OF_NAME)) {
				assert staticInvokeExpr.getArgCount() == 2;

				valueBox.setValue(staticInvokeExpr.getArg(1));
			}
		}
	}

}
