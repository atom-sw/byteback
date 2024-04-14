package byteback.syntax.value.transformer;

import byteback.syntax.Vimp;
import byteback.syntax.name.BBLibNames;
import byteback.common.function.Lazy;
import byteback.syntax.value.context.ValueContext;
import soot.*;
import soot.jimple.InvokeExpr;

/**
 * Transforms invocation to the byteback.specification.Special.condition method into the ternary operator.
 *
 * @author paganma
 */
public class ConditionalExprTransformer extends ValueTransformer {

    private static final Lazy<ConditionalExprTransformer> INSTANCE = Lazy.from(ConditionalExprTransformer::new);

    public static ConditionalExprTransformer v() {
        return INSTANCE.get();
    }

    private ConditionalExprTransformer() {
    }

    @Override
    public void transformValue(final ValueContext valueContext) {
        final ValueBox valueBox = valueContext.getValueBox();
        final Value value = valueBox.getValue();

        if (value instanceof InvokeExpr invokeExpr) {
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
