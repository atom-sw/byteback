package byteback.analysis.body.vimp.transformer;

import byteback.common.function.Lazy;

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
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
    public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        for (final ValueBox valueBox : body.getUseBoxes()) {
            final Value value = valueBox.getValue();

            if (value instanceof DynamicInvokeExpr invokeDynamicExpr) {
                valueBox.setValue(Jimple.v().newStaticInvokeExpr(invokeDynamicExpr.getMethodRef(), invokeDynamicExpr.getArgs()));
            }
        }
    }

}
