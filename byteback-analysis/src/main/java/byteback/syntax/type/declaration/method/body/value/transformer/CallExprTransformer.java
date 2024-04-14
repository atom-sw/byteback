package byteback.syntax.type.declaration.method.body.value.transformer;

import byteback.syntax.tag.AnnotationReader;
import byteback.syntax.value.CallExpr;
import byteback.syntax.value.context.ValueContext;
import byteback.syntax.value.transformer.ValueTransformer;
import byteback.syntax.Vimp;
import byteback.syntax.name.BBLibNames;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;

import java.util.ArrayList;

/**
 * Transforms invocations to behavioral functions to pure (mathematical) function calls.
 *
 * @author paganma
 */
public class CallExprTransformer extends ValueTransformer {

    private static final Lazy<CallExprTransformer> INSTANCE = Lazy.from(CallExprTransformer::new);

    public static CallExprTransformer v() {
        return INSTANCE.get();
    }

    private CallExprTransformer() {
    }

    @Override
    public void transformValue(final ValueContext valueContext) {
        final ValueBox valueBox = valueContext.getValueBox();
        final Value value = valueBox.getValue();

        if (value instanceof final InvokeExpr invokeExpr) {
            final SootMethodRef invokedMethodRef = invokeExpr.getMethodRef();
            final SootMethod invokedMethod = invokedMethodRef.resolve();

            if (AnnotationReader.v().hasAnnotation(invokedMethod, BBLibNames.BEHAVIOR_ANNOTATION)) {
                final var args = new ArrayList<>(invokeExpr.getArgs());

                if (invokeExpr instanceof final InstanceInvokeExpr instanceInvokeExpr) {
                    args.add(0, instanceInvokeExpr.getBase());
                }

                final CallExpr callExpr = Vimp.v().newCallExpr(invokedMethodRef, args);
                valueBox.setValue(callExpr);
            }
        }
    }

}