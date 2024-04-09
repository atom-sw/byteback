package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.common.tag.AnnotationReader;
import byteback.analysis.local.common.transformer.value.ValueTransformer;
import byteback.analysis.local.vimp.syntax.Vimp;
import byteback.analysis.common.name.BBLibNames;
import byteback.common.function.Lazy;
import soot.Body;
import soot.SootMethod;
import soot.UnitBox;
import soot.ValueBox;
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
            final SootMethod invokedMethod = invokeExpr.getMethod();

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
