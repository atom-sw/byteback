package byteback.syntax.member.method.body.transformer;

import byteback.syntax.tag.AnnotationReader;
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

    private static final Lazy<CallExprTransformer> instance = Lazy.from(CallExprTransformer::new);

    public static CallExprTransformer v() {
        return instance.get();
    }

    private CallExprTransformer() {
    }

    @Override
    public void transformValue(final Body body, final UnitBox unitBox, final ValueBox valueBox) {
        if (valueBox.getValue() instanceof InvokeExpr invokeExpr) {
            final SootMethodRef invokedMethodRef = invokeExpr.getMethodRef();
            final SootMethod invokedMethod = invokedMethodRef.resolve();

            if (AnnotationReader.v().hasAnnotation(invokedMethod, BBLibNames.BEHAVIOR_ANNOTATION)) {
                final var args = new ArrayList<>(invokeExpr.getArgs());

                if (invokeExpr instanceof final InstanceInvokeExpr instanceInvokeExpr) {
                    args.add(0, instanceInvokeExpr.getBase());
                }

                valueBox.setValue(Vimp.v().newCallExpr(invokeExpr.getMethodRef(), args));
            }
        }
    }

}