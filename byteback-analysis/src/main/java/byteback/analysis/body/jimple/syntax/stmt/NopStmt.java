package byteback.analysis.body.jimple.syntax.stmt;

import byteback.analysis.body.common.syntax.stmt.Stmt;

public class NopStmt extends Stmt {

    public NopStmt() {
    }

    @Override
    public Object clone() {
        return new NopStmt();
    }

    @Override
    public String toString() {
        return "nop";
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
