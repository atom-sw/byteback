package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.syntax.Unit;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.vimp.syntax.SpecificationStmt;
import byteback.common.function.Lazy;
import byteback.analysis.body.jimple.syntax.Unit;

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
