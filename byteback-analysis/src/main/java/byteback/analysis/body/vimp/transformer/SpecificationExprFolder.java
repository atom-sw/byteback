package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.vimp.syntax.SpecificationStmt;
import byteback.common.function.Lazy;
import soot.Unit;
import soot.ValueBox;

public class SpecificationExprFolder extends ExprFolder {

    private static final Lazy<SpecificationExprFolder> instance = Lazy.from(SpecificationExprFolder::new);

    public static SpecificationExprFolder v() {
        return instance.get();
    }

    @Override
    public boolean canSubstitute(final Unit unit, final ValueBox valueBox) {
        return unit instanceof SpecificationStmt;
    }

}
