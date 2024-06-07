package byteback.syntax.scene.type.declaration.member.method.body.value.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.CallExpr;
import byteback.syntax.scene.type.declaration.member.method.body.value.OldExpr;
import soot.Body;
import soot.SootMethod;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ConcreteRef;

/**
 * Transforms invocations to behavioral functions to pure (mathematical)
 * function calls.
 *
 * @author paganma
 */
public class OldExprTightener extends ValueTransformer {

	private static final Lazy<OldExprTightener> INSTANCE = Lazy.from(OldExprTightener::new);

	public static OldExprTightener v() {
		return INSTANCE.get();
	}

	private OldExprTightener() {
	}

	@Override
	public void transformValue(final SootMethod sootMethod, final Body body, final ValueBox valueBox) {
		final Value value = valueBox.getValue();

		if (value instanceof final OldExpr oldExpr) {
			final Value opValue = oldExpr.getOp();

			for (final ValueBox useBox : opValue.getUseBoxes()) {
				final Value useValue = useBox.getValue();

				if (useValue instanceof ConcreteRef || useValue instanceof CallExpr) {
					final OldExpr newOldExpr = Vimp.v().newOldExpr(useValue);
					useBox.setValue(newOldExpr);
				}
			}

			valueBox.setValue(opValue);
		}
	}

}
