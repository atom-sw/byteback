package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.expr.Value;

public class InvariantStmt extends SpecificationStmt {

    public InvariantStmt(final Value condition) {
        super(condition);
    }
}
