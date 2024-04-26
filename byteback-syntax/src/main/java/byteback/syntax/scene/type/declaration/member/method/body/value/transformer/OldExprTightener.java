package byteback.syntax.scene.type.declaration.member.method.body.value.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.CallExpr;
import byteback.syntax.scene.type.declaration.member.method.body.value.OldExpr;
import byteback.syntax.scene.type.declaration.member.method.body.value.context.ValueContext;
import byteback.syntax.tag.AnnotationTagReader;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ConcreteRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;

import java.util.ArrayList;

/**
 * Transforms invocations to behavioral functions to pure (mathematical) function calls.
 *
 * @author paganma
 */
public class OldExprTightener extends ValueTransformer {

    private static final Lazy<OldExprTightener> INSTANCE = Lazy.from(OldExprTightener::new);

    public static OldExprTightener v() {
        return INSTANCE.get();
    }

    private OldExprTightener() {
    }

    @Override
    public void transformValue(final ValueContext valueContext) {
        final ValueBox valueBox = valueContext.getValueBox();
        final Value value = valueBox.getValue();

        if (value instanceof final OldExpr oldExpr) {
            final Value opValue = oldExpr.getOp();

            for (final ValueBox useBox : opValue.getUseBoxes()) {
                final Value useValue = useBox.getValue();

                if (useValue instanceof ConcreteRef || useValue instanceof CallExpr) {
                    useBox.setValue(Vimp.v().newOldExpr(useValue));
                }
            }

            valueBox.setValue(opValue);
        }
    }

}