package byteback.syntax.value.transformer;

import byteback.syntax.name.BBLibNames;
import byteback.syntax.Vimp;
import byteback.syntax.member.method.body.tag.ExceptionalFlagger;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.InvokeExpr;

/**
 * @author paganma
 */
public class ThrownExprTransformer extends ValueTransformer {

    private static final Lazy<ThrownExprTransformer> instance = Lazy.from(ThrownExprTransformer::new);

    public static ThrownExprTransformer v() {
        return instance.get();
    }

    private ThrownExprTransformer() {
    }

    @Override
    public void transformValue(final Body body, final UnitBox unitBox, final ValueBox valueBox) {
        if (valueBox.getValue() instanceof InvokeExpr invokeExpr) {
            final SootMethodRef invokedMethodRef = invokeExpr.getMethodRef();

            if (BBLibNames.v().isSpecialClass(invokedMethodRef.getDeclaringClass())) {

                if (invokedMethodRef.getName().equals("thrown")) {
                    valueBox.setValue(Vimp.v().newCaughtExceptionRef());
                    ExceptionalFlagger.v().flag(body);
                }
            }
        }
    }

}
