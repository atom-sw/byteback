package byteback.syntax.scene.type.declaration.member.method.body.value.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.ThrownRef;
import byteback.syntax.scene.type.declaration.member.method.body.value.context.ValueContext;
import soot.Body;
import soot.SootMethodRef;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;

/**
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
    public void transformValue(final ValueContext valueContext) {
        final ValueBox valueBox = valueContext.getValueBox();
        final Value value = valueBox.getValue();

        if (value instanceof final InvokeExpr invokeExpr) {
            final SootMethodRef invokedMethodRef = invokeExpr.getMethodRef();

            if (BBLibNames.v().isSpecialClass(invokedMethodRef.getDeclaringClass())) {
                if (invokedMethodRef.getName().equals(BBLibNames.THROWN_NAME)) {
                    final ThrownRef thrownRef = Vimp.v().newThrownRef();
                    valueBox.setValue(thrownRef);
                }
            }
        }
    }

}
