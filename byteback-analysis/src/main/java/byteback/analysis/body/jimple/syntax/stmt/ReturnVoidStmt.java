package byteback.analysis.body.jimple.syntax.stmt;

import byteback.analysis.body.common.syntax.stmt.Stmt;

public class ReturnVoidStmt extends Stmt {

    public ReturnVoidStmt() {
    }

    @Override
    public String toString() {
        return "return";
    }

    @Override
    public boolean fallsThrough() {
        return false;
    }

    @Override
    public boolean branches() {
        return false;
    }
}
