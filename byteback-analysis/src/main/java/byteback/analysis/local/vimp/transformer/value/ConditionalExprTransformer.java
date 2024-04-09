package byteback.analysis.local.vimp.transformer.value;

import byteback.analysis.local.common.transformer.value.ValueTransformer;
import byteback.analysis.local.vimp.syntax.Vimp;
import byteback.analysis.common.name.BBLibNames;
import byteback.common.function.Lazy;
import soot.Body;
import soot.SootMethodRef;
import soot.UnitBox;
import soot.ValueBox;
import soot.jimple.InvokeExpr;

/**
 * Transforms invocation to the byteback.specification.Special.condition method into the ternary operator.
 *
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
    public void transformValue(final Body body, final UnitBox unitBox, final ValueBox valueBox) {
        if (valueBox.getValue() instanceof InvokeExpr invokeExpr) {
            final SootMethodRef invokedMethodRef = invokeExpr.getMethodRef();

            if (BBLibNames.v().isSpecialClass(invokedMethodRef.getDeclaringClass())) {
                if (invokedMethodRef.getName().equals("conditional")) {
                    assert invokeExpr.getArgs().size() == 3
                            : "BBLib's definition of conditional takes only 3 arguments";
                    valueBox.setValue(
                            Vimp.v().newConditionExpr(
                                    invokeExpr.getArg(0),
                                    invokeExpr.getArg(1),
                                    invokeExpr.getArg(2)
                            )
                    );
                }
            }
        }
    }

}