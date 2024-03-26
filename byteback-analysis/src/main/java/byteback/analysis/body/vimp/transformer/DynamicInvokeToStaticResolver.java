package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.common.function.Lazy;

import soot.Body;
import soot.Value;
import soot.ValueBox;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.Jimple;

/**
 * Converts dynamic invocations into static invocations to the related instance method. This is done to handle the
 * conversion of invocations to the methods of Soot's soot.dummy.InvokeDynamic class.
 * @author paganma
 */
public class DynamicInvokeToStaticResolver extends BodyTransformer {

    private static final Lazy<DynamicInvokeToStaticResolver> instance = Lazy.from(DynamicInvokeToStaticResolver::new);

    private DynamicInvokeToStaticResolver() {
    }

    public static DynamicInvokeToStaticResolver v() {
        return instance.get();
    }

    @Override
    public void transformBody(final Body body) {
        for (final ValueBox valueBox : body.getUseBoxes()) {
            final Value value = valueBox.getValue();

            if (value instanceof DynamicInvokeExpr invokeDynamicExpr) {
                valueBox.setValue(Jimple.v().newStaticInvokeExpr(invokeDynamicExpr.getMethodRef(),
                        invokeDynamicExpr.getArgs()));
            }
        }
    }

}
