package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.local.vimp.syntax.value.ReturnRef;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.AssignStmt;

/**
 * Folds the expressions in a behavior method.
 *
 * @author paganma
 */
public class BehaviorExprFolder extends ExprFolder {

    private static final Lazy<BehaviorExprFolder> instance = Lazy.from(BehaviorExprFolder::new);

    public static BehaviorExprFolder v() {
        return instance.get();
    }

    @Override
    public boolean canSubstituteUse(final Unit unit, final ValueBox valueBox) {
        return unit instanceof final AssignStmt assignStmt && assignStmt.getLeftOp() instanceof ReturnRef;
    }

}
