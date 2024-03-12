package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.common.function.Lazy;
import soot.Body;
import soot.Value;
import soot.ValueBox;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.Jimple;

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
