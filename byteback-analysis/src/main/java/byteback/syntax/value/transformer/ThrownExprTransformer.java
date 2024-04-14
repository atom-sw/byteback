package byteback.syntax.value.transformer;

import byteback.syntax.name.BBLibNames;
import byteback.syntax.Vimp;
import byteback.syntax.type.declaration.method.body.tag.ExceptionalFlagger;
import byteback.common.function.Lazy;
import byteback.syntax.value.context.ValueContext;
import soot.*;
import soot.jimple.InvokeExpr;

/**
 *
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
    public void transformValue(final ValueContext valueContext) {
        final ValueBox valueBox = valueContext.getValueBox();
        final Value value = valueBox.getValue();

        if (value instanceof final InvokeExpr invokeExpr) {
            final SootMethodRef invokedMethodRef = invokeExpr.getMethodRef();

            if (BBLibNames.v().isSpecialClass(invokedMethodRef.getDeclaringClass())) {
                if (invokedMethodRef.getName().equals("thrown")) {
                    final Body body = valueContext.getBody();
                    valueBox.setValue(Vimp.v().newCaughtExceptionRef());
                    ExceptionalFlagger.v().flag(body);
                }
            }
        }
    }

}
