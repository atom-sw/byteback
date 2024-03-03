package byteback.analysis.body.jimple.transformer;

import byteback.analysis.body.common.transformer.ValueTransformer;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.common.namespace.BBLibNames;
import byteback.common.function.Lazy;
import soot.SootMethod;
import soot.ValueBox;
import soot.jimple.InvokeExpr;

public class OldExprTransformer extends ValueTransformer {

    private static final Lazy<OldExprTransformer> instance = Lazy.from(OldExprTransformer::new);

    public static OldExprTransformer v() {
        return instance.get();
    }

    private OldExprTransformer() {
    }

    @Override
    public void transformValue(ValueBox valueBox) {
        if (valueBox.getValue() instanceof InvokeExpr invokeExpr) {

            final SootMethod invokedMethod = invokeExpr.getMethod();

            if (BBLibNames.isSpecialClass(invokedMethod.getDeclaringClass())) {
                final SootMethod method = invokeExpr.getMethod();

                if (method.getName().equals("old")) {
                    valueBox.setValue(Vimp.v().newOldExpr(invokeExpr.getArg(0)));
                } else {
                    throw new IllegalStateException("Unknown special method " + method);
                }
            }

        }
    }
}
