package byteback.syntax.value.transformer;

import byteback.syntax.Vimp;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.member.method.body.tag.TwoStateFlagger;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.InvokeExpr;

/**
 * Transforms invocations to byteback.specification.Special.old into the `old` operator, which can refer to the
 * pre-state in a postcondition.
 *
 * @author paganma
 */
public class OldExprTransformer extends ValueTransformer {

    private static final Lazy<OldExprTransformer> instance = Lazy.from(OldExprTransformer::new);

    public static OldExprTransformer v() {
        return instance.get();
    }

    private OldExprTransformer() {
    }

    @Override
    public void transformValue(final Body body, final UnitBox unitBox, final ValueBox valueBox) {
        if (valueBox.getValue() instanceof InvokeExpr invokeExpr) {
            final SootMethodRef invokedMethodRef = invokeExpr.getMethodRef();

            if (BBLibNames.v().isSpecialClass(invokedMethodRef.getDeclaringClass())) {
                if (invokedMethodRef.getName().equals("old")) {
                    valueBox.setValue(Vimp.v().newOldExpr(invokeExpr.getArg(0)));
                    TwoStateFlagger.v().flag(body);
                }
            }
        }
    }

}