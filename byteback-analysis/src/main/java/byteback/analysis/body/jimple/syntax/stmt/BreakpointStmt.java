package byteback.analysis.body.jimple.syntax.stmt;

import byteback.analysis.body.common.syntax.stmt.Stmt;

public class BreakpointStmt extends Stmt {

    public BreakpointStmt() {
    }

    @Override
    public String toString() {
        return "break;";
    }

    @Override
    public boolean fallsThrough() {
        return true;
    }

    @Override
    public boolean branches() {
        return false;
    }
}
