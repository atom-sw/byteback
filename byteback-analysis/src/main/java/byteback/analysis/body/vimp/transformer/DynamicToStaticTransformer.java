package byteback.analysis.body.vimp.transformer;

import byteback.common.Lazy;

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Value;
import soot.ValueBox;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.Jimple;

public class DynamicToStaticTransformer extends BodyTransformer {

    private static final Lazy<DynamicToStaticTransformer> instance = Lazy.from(DynamicToStaticTransformer::new);

    private DynamicToStaticTransformer() {
    }

    public static DynamicToStaticTransformer v() {
        return instance.get();
    }

    @Override
    public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        transformBody(body);
    }

    public void transformBody(final Body body) {
        for (final ValueBox vbox : body.getUseBoxes()) {
            final Value value = vbox.getValue();

            if (value instanceof DynamicInvokeExpr invokeDynamicExpr) {
                vbox.setValue(Jimple.v().newStaticInvokeExpr(invokeDynamicExpr.getMethodRef(), invokeDynamicExpr.getArgs()));
            }
        }
    }

}
