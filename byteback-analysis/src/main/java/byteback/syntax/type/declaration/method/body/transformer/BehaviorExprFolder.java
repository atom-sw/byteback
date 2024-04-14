package byteback.syntax.type.declaration.method.body.transformer;

import byteback.syntax.type.declaration.method.body.context.BodyContext;
import byteback.syntax.type.declaration.method.body.tag.BehaviorFlagger;
import byteback.syntax.value.ReturnRef;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.AssignStmt;

/**
 * Folds the expressions in a behavior method.
 *
 * @author paganma
 */
public class BehaviorExprFolder extends ExprFolder {

    private static final Lazy<BehaviorExprFolder> INSTANCE = Lazy.from(BehaviorExprFolder::new);

    public static BehaviorExprFolder v() {
        return INSTANCE.get();
    }

    @Override
    public void walkBody(final BodyContext bodyContext) {
        final Body body = bodyContext.getBody();

        if (BehaviorFlagger.v().isTagged(body)) {
            super.walkBody(bodyContext);
        }
    }

    @Override
    public boolean canSubstituteUse(final Unit unit, final ValueBox valueBox) {
        return unit instanceof final AssignStmt assignStmt
                && assignStmt.getLeftOp() instanceof ReturnRef;
    }

}
