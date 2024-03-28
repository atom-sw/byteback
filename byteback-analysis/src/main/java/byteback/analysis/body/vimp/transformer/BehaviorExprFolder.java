package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.vimp.tag.BehaviorMethodTag;
import byteback.analysis.common.Hosts;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.ReturnStmt;

/**
 * Folder for the expressions in a behavior method.
 *
 * @author paganma
 */
public class BehaviorExprFolder extends ExprFolder {

    private static final Lazy<BehaviorExprFolder> instance = Lazy.from(BehaviorExprFolder::new);

    public static BehaviorExprFolder v() {
        return instance.get();
    }

    @Override
    public boolean canSubstitute(final Unit unit, final ValueBox valueBox) {
        return unit instanceof ReturnStmt;
    }

    @Override
    public void transformBody(final Body body) {
        if (Hosts.v().hasTag(body.getMethod(), BehaviorMethodTag.NAME)) {
            super.transformBody(body);
        }
    }

}
