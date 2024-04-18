package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.tag.BehaviorFlagger;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.ReturnRef;
import soot.Body;
import soot.Unit;
import soot.ValueBox;
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
    public void transformBody(final BodyContext bodyContext) {
        if (BehaviorFlagger.v().isTagged(bodyContext.getSootMethod())) {
            super.transformBody(bodyContext);
        }
    }

    @Override
    public boolean canSubstituteUse(final Unit unit, final ValueBox valueBox) {
        return unit instanceof final AssignStmt assignStmt
                && assignStmt.getLeftOp() instanceof ReturnRef;
    }

}
