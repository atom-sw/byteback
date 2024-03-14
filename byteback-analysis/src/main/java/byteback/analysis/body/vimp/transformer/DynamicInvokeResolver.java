package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.jimple.syntax.expr.DynamicInvokeExpr;
import byteback.analysis.body.jimple.syntax.expr.StaticInvokeExpr;
import byteback.common.function.Lazy;
import byteback.analysis.body.common.syntax.Body;
import byteback.analysis.body.common.syntax.expr.Value;

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
                valueBox.setValue(new StaticInvokeExpr(invokeDynamicExpr.getSignature(), invokeDynamicExpr.getArgs()));
            }
        }
    }

}
