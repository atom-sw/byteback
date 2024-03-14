package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.expr.Value;

public class AssertStmt extends SpecificationStmt {

    public AssertStmt(final Value condition) {
        super(condition);
    }
}
