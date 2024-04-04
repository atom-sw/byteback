package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.local.vimp.syntax.unit.SpecificationStmt;
import byteback.common.function.Lazy;
import soot.Unit;
import soot.ValueBox;

/**
 * Folder for the specification expressions that appear in the body of a procedural method.
 *
 * @author paganma
 */
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
