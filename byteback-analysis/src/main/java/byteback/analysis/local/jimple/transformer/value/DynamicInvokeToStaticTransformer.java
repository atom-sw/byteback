package byteback.analysis.local.jimple.transformer.value;

import byteback.analysis.local.common.transformer.value.UseValueTransformer;
import byteback.common.function.Lazy;

import soot.*;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.StaticInvokeExpr;

import java.util.List;

/**
 * Converts dynamic invocations into static invocations to the related instance method. This is done to handle the
 * conversion of invocations to the methods of Soot's soot.dummy.InvokeDynamic class.
 *
 * @author paganma
 */
public class DynamicInvokeToStaticTransformer extends UseValueTransformer {

    private static final Lazy<DynamicInvokeToStaticTransformer> instance = Lazy.from(DynamicInvokeToStaticTransformer::new);

    private DynamicInvokeToStaticTransformer() {
    }

    public static DynamicInvokeToStaticTransformer v() {
        return instance.get();
    }

    @Override
    public void transformValue(final Body body, final UnitBox unitBox, final ValueBox valueBox) {
        final Value value = valueBox.getValue();

        if (value instanceof DynamicInvokeExpr invokeDynamicExpr) {
            final SootMethodRef invokedMethodRef = invokeDynamicExpr.getMethodRef();
            final List<Value> invokeArgs = invokeDynamicExpr.getArgs();
            final StaticInvokeExpr staticInvokeExpr = Jimple.v().newStaticInvokeExpr(invokedMethodRef, invokeArgs);
            valueBox.setValue(staticInvokeExpr);
        }
    }

}