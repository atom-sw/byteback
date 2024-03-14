package byteback.analysis.body.jimple.transformer;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.common.transformer.ValueTransformer;
import byteback.analysis.body.vimp.syntax.OldExpr;
import byteback.analysis.common.naming.BBLibNames;
import byteback.analysis.model.syntax.signature.MethodSignature;
import byteback.common.function.Lazy;
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
            final MethodSignature invokedSignature = invokeExpr.getSignature();

            if (BBLibNames.v().isSpecialClass(invokedSignature.getDeclaringClassType())) {
                if (invokedSignature.getName().equals("condition")) {
                    valueBox.setValue(new OldExpr(invokeExpr.getArg(0)));
                }
            }
        }
    }
}
