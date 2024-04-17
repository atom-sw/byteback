package byteback.syntax.scene.type.declaration.member.method.body.value.transformer;

import byteback.common.function.Lazy;

import byteback.syntax.scene.type.declaration.member.method.body.value.transformer.context.ValueTransformationContext;
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
public class DynamicInvokeToStaticTransformer extends ValueTransformer {

    private static final Lazy<DynamicInvokeToStaticTransformer> INSTANCE = Lazy.from(DynamicInvokeToStaticTransformer::new);

    private DynamicInvokeToStaticTransformer() {
    }

    public static DynamicInvokeToStaticTransformer v() {
        return INSTANCE.get();
    }

    @Override
    public void transformValue(final ValueTransformationContext valueContext) {
        final ValueBox valueBox = valueContext.getValueBox();
        final Value value = valueBox.getValue();

        if (value instanceof final DynamicInvokeExpr invokeDynamicExpr) {
            final SootMethodRef invokedMethodRef = invokeDynamicExpr.getMethodRef();
            final List<Value> invokeArgs = invokeDynamicExpr.getArgs();
            final StaticInvokeExpr staticInvokeExpr = Jimple.v().newStaticInvokeExpr(invokedMethodRef, invokeArgs);
            valueBox.setValue(staticInvokeExpr);
        }
    }

}
