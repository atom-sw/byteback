package byteback.analysis.local.vimp.transformer.value;

import byteback.analysis.common.name.BBLibNames;
import byteback.analysis.local.common.transformer.value.ValueTransformer;
import byteback.analysis.local.vimp.syntax.Vimp;
import byteback.analysis.local.vimp.tag.body.TwoStateFlagger;
import byteback.common.function.Lazy;
import soot.Body;
import soot.SootMethod;
import soot.UnitBox;
import soot.ValueBox;
import soot.jimple.InvokeExpr;

/**
 * Transforms invocations to byteback.specification.Special.old into the `old` operator, which can refer to the
 * pre-state in a postcondition.
 *
 * @author paganma
 */
public class ThrownExprTransformer extends ValueTransformer {

    private static final Lazy<ThrownExprTransformer> instance = Lazy.from(ThrownExprTransformer::new);

    public static ThrownExprTransformer v() {
        return instance.get();
    }

    private ThrownExprTransformer() {
    }

    @Override
    public void transformValue(final Body body, final UnitBox unitBox, final ValueBox valueBox) {
        if (valueBox.getValue() instanceof InvokeExpr invokeExpr) {
            final SootMethod invokedMethod = invokeExpr.getMethod();

            if (BBLibNames.v().isSpecialClass(invokedMethod.getDeclaringClass())) {
                final SootMethod method = invokeExpr.getMethod();

                if (method.getName().equals("thrown")) {
                    valueBox.setValue(Vimp.v().newOldExpr(invokeExpr.getArg(0)));
                    TwoStateFlagger.v().flag(body);
                }
            }
        }
    }

}
