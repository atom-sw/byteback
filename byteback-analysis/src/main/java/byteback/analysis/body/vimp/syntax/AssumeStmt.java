package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.expr.Value;

public class AssumeStmt extends SpecificationStmt {

    public AssumeStmt(final Value condition) {
        super(condition);
    }
}
