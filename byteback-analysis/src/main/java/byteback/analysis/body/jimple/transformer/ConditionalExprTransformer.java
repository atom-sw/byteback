package byteback.analysis.body.jimple.transformer;

import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.common.transformer.ValueTransformer;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.common.naming.BBLibNames;
import byteback.common.function.Lazy;
import byteback.analysis.model.syntax.MethodModel;
import byteback.analysis.body.jimple.syntax.expr.InvokeExpr;

public class ConditionalExprTransformer extends ValueTransformer {

    private static final Lazy<ConditionalExprTransformer> instance = Lazy.from(ConditionalExprTransformer::new);

    public static ConditionalExprTransformer v() {
        return instance.get();
    }

    private ConditionalExprTransformer() {
    }

    @Override
    public void transformValue(final ValueBox valueBox) {
        if (valueBox.getValue() instanceof InvokeExpr invokeExpr) {
            final MethodModel invokedMethod = invokeExpr.getMethod();

            if (BBLibNames.v().isSpecialClass(invokedMethod.getDeclaringClass())) {
                final MethodModel method = invokeExpr.getMethod();

                if (method.getName().equals("condition")) {
                    valueBox.setValue(Vimp.v().newOldExpr(invokeExpr.getArg(0)));
                }
            }
        }
    }
}
