package byteback.syntax.scene.type.declaration.member.method.body.value.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.body.value.CallExpr;
import byteback.syntax.scene.type.declaration.member.method.body.value.context.ValueContext;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import byteback.syntax.tag.AnnotationTagReader;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;

import java.util.ArrayList;

/**
 * Transforms invocations to behavioral functions to pure (mathematical) function calls.
 *
 * @author paganma
 */
public class CallExprTransformer extends ValueTransformer {

    private static final Lazy<CallExprTransformer> INSTANCE = Lazy.from(CallExprTransformer::new);

    public static CallExprTransformer v() {
        return INSTANCE.get();
    }

    private CallExprTransformer() {
    }

    @Override
    public void transformValue(final ValueContext valueContext) {
        final ValueBox valueBox = valueContext.getValueBox();
        final Value value = valueBox.getValue();

        if (value instanceof final InvokeExpr invokeExpr) {
            final SootMethodRef invokedMethodRef = invokeExpr.getMethodRef();
            final SootMethod invokedMethod = invokedMethodRef.resolve();

            if (BehaviorTagMarker.v().hasTag(invokedMethod)) {
                final var args = new ArrayList<>(invokeExpr.getArgs());

                if (invokeExpr instanceof final InstanceInvokeExpr instanceInvokeExpr) {
                    args.add(0, instanceInvokeExpr.getBase());
                }

                final CallExpr callExpr = Vimp.v().newCallExpr(invokedMethodRef, args);
                valueBox.setValue(callExpr);
            }
        }
    }

}