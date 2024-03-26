package byteback.analysis.body.jimple.transformer;

import byteback.analysis.body.common.transformer.ValueTransformer;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.common.namespace.BBLibNames;
import byteback.common.function.Lazy;
import soot.SootMethod;
import soot.ValueBox;
import soot.jimple.InvokeExpr;

/**
 * Transforms invocation to the byteback.specification.Special.condition method into the ternary operator.
 * @author paganma
 */
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
            final SootMethod invokedMethod = invokeExpr.getMethod();

            if (BBLibNames.v().isSpecialClass(invokedMethod.getDeclaringClass())) {
                final SootMethod method = invokeExpr.getMethod();

                if (method.getName().equals("condition")) {
                    /* TODO */
                }
            }
        }
    }
}
