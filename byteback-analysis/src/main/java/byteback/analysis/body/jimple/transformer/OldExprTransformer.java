package byteback.analysis.body.jimple.transformer;

import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.common.transformer.ValueTransformer;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.common.naming.BBLibNames;
import byteback.common.function.Lazy;
import byteback.analysis.model.syntax.MethodModel;
import byteback.analysis.body.jimple.syntax.expr.InvokeExpr;

public class OldExprTransformer extends ValueTransformer {

    private static final Lazy<OldExprTransformer> instance = Lazy.from(OldExprTransformer::new);

    public static OldExprTransformer v() {
        return instance.get();
    }

    private OldExprTransformer() {
    }

    @Override
    public void transformValue(final ValueBox valueBox) {
        if (valueBox.getValue() instanceof InvokeExpr invokeExpr) {
            final MethodModel invokedMethod = invokeExpr.getMethod();

            if (BBLibNames.v().isSpecialClass(invokedMethod.getDeclaringClass())) {
                final MethodModel method = invokeExpr.getMethod();

                if (method.getName().equals("old")) {
                    valueBox.setValue(Vimp.v().newOldExpr(invokeExpr.getArg(0)));
                }
            }
        }
    }

}
