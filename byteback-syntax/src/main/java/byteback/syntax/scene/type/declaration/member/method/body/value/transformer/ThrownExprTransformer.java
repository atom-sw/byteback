package byteback.syntax.scene.type.declaration.member.method.body.value.transformer;

import byteback.syntax.name.BBLibNames;
import byteback.syntax.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.tag.ExceptionalFlagger;
import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.context.BodyTransformerContext;
import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.context.UnitTransformerContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.transformer.context.ValueTransformerContext;
import soot.*;
import soot.jimple.InvokeExpr;

/**
 *
 * @author paganma
 */
public class ThrownExprTransformer extends ValueTransformer {

    private static final Lazy<ThrownExprTransformer> INSTANCE = Lazy.from(ThrownExprTransformer::new);

    public static ThrownExprTransformer v() {
        return INSTANCE.get();
    }

    private ThrownExprTransformer() {
    }

    @Override
    public void walkValue(final ValueTransformerContext valueContext) {
        final UnitTransformerContext unitContext = valueContext.getUnitContext();
        final BodyTransformerContext bodyContext = unitContext.getBodyContext();
        final ValueBox valueBox = valueContext.getValueBox();
        final Value value = valueBox.getValue();

        if (value instanceof final InvokeExpr invokeExpr) {
            final SootMethodRef invokedMethodRef = invokeExpr.getMethodRef();

            if (BBLibNames.v().isSpecialClass(invokedMethodRef.getDeclaringClass())) {
                if (invokedMethodRef.getName().equals("thrown")) {
                    final Body body = bodyContext.getBody();
                    valueBox.setValue(Vimp.v().newCaughtExceptionRef());
                    ExceptionalFlagger.v().flag(body);
                }
            }
        }
    }

}
