package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.common.function.Lazy;
import byteback.analysis.body.common.Body;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.jimple.syntax.DynamicInvokeExpr;
import byteback.analysis.body.jimple.syntax.Jimple;

public class DynamicInvokeResolver extends BodyTransformer {

    private static final Lazy<DynamicInvokeResolver> instance = Lazy.from(DynamicInvokeResolver::new);

    private DynamicInvokeResolver() {
    }

    public static DynamicInvokeResolver v() {
        return instance.get();
    }

    @Override
    public void transformBody(final Body body) {
        for (final ValueBox valueBox : body.getUseBoxes()) {
            final Value value = valueBox.getValue();

            if (value instanceof DynamicInvokeExpr invokeDynamicExpr) {
                valueBox.setValue(Jimple.v().newStaticInvokeExpr(invokeDynamicExpr.getMethodRef(), invokeDynamicExpr.getArgs()));
            }
        }
    }

}
