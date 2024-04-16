package byteback.syntax.scene.type.declaration.member.method.body.value.transformer;

import byteback.syntax.Vimp;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.body.tag.TwoStateFlagger;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.context.BodyTransformerContext;
import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.context.UnitTransformerContext;
import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.value.transformer.context.ValueTransformerContext;
import soot.*;
import soot.jimple.InvokeExpr;

/**
 * Transforms invocations to byteback.specification.Special.old into the `old` operator, which can refer to the
 * pre-state in a postcondition.
 *
 * @author paganma
 */
public class OldExprTransformer extends ValueTransformer {

    private static final Lazy<OldExprTransformer> INSTANCE = Lazy.from(OldExprTransformer::new);

    public static OldExprTransformer v() {
        return INSTANCE.get();
    }

    private OldExprTransformer() {
    }

    @Override
    public void walkValue(final ValueTransformerContext valueContext) {
        final UnitTransformerContext unitContext = valueContext.getUnitContext();
        final BodyTransformerContext bodyContext = unitContext.getBodyContext();
        final ValueBox valueBox = valueContext.getValueBox();
        final Value value = valueBox.getValue();

        if (value instanceof final InvokeExpr invokeExpr) {
            final SootMethodRef invokedMethodRef = invokeExpr.getMethodRef();
            final SootClass declaringClass = invokedMethodRef.getDeclaringClass();

            if (BBLibNames.v().isSpecialClass(declaringClass)) {
                if (invokedMethodRef.getName().equals("old")) {
                    final Body body = bodyContext.getBody();
                    valueBox.setValue(Vimp.v().newOldExpr(invokeExpr.getArg(0)));
                    TwoStateFlagger.v().flag(body);
                }
            }
        }
    }

}