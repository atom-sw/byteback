package byteback.analysis.body.jimple.transformer;

import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.common.transformer.ValueTransformer;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.common.naming.BBLibNames;
import byteback.common.function.Lazy;
import byteback.analysis.model.syntax.MethodModel;
import byteback.analysis.body.jimple.syntax.expr.InstanceInvokeExpr;
import byteback.analysis.body.jimple.syntax.expr.InvokeExpr;

import java.util.ArrayList;

public class CallExprTransformer extends ValueTransformer {

    private static final Lazy<CallExprTransformer> instance = Lazy.from(CallExprTransformer::new);

    public static CallExprTransformer v() {
        return instance.get();
    }

    private CallExprTransformer() {
    }

    @Override
    public void transformValue(final ValueBox valueBox) {
        if (valueBox.getValue() instanceof InvokeExpr invokeExpr) {
            final MethodModel invokedMethod = invokeExpr.getMethod();

            if (BBLibNames.v().isFunctionMethod(invokedMethod)) {
                final var args = new ArrayList<>(invokeExpr.getArgs());

                if (invokeExpr instanceof InstanceInvokeExpr instanceInvokeExpr) {
                    args.add(0, instanceInvokeExpr.getBase());
                }

                valueBox.setValue(Vimp.v().newCallExpr(invokeExpr.getMethodRef(), args));
            }
        }
    }

}
