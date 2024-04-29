package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import soot.SootMethod;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.ReturnStmt;

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
        final SootMethod sootMethod = bodyContext.getSootMethod();

        if (BehaviorTagMarker.v().hasTag(sootMethod)) {
            super.transformBody(bodyContext);
        }
    }

    @Override
    public boolean canSubstituteUse(final Unit unit, final ValueBox valueBox) {
        return unit instanceof ReturnStmt;
    }

}
