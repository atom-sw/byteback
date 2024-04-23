package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.NestedExpr;
import byteback.syntax.scene.type.declaration.member.method.body.value.OldExpr;
import byteback.syntax.scene.type.declaration.member.method.body.value.context.ValueContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.transformer.ValueTransformer;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ConcreteRef;

public class OldExprTightener extends ValueTransformer {

    public static Lazy<OldExprTightener> INSTANCE = Lazy.from(OldExprTightener::new);

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
            for (final ValueBox useBox : oldExpr.getUseBoxes()) {
                Value useValue = useBox.getValue();

                if (useValue instanceof final OldExpr nestedOldExpr) {
                    useBox.setValue(nestedOldExpr.getOp());
                } else if (useValue instanceof ConcreteRef) {
                    while (useValue instanceof final NestedExpr nestedExpr) {
                        useValue = nestedExpr.getValue();
                    }

                    final OldExpr newOldExpr = Vimp.v().newOldExpr(useValue);
                    useBox.setValue(newOldExpr);
                }
            }
        }
    }

}
