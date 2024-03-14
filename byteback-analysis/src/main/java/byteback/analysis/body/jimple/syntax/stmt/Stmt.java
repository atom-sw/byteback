package byteback.analysis.body.jimple.syntax.stmt;

import byteback.analysis.body.common.syntax.Unit;

public abstract class Stmt extends Unit {

    /**
     * Returns true if execution after this statement may continue at the following statement. GotoStmt will return false but
     * IfStmt will return true.
     */
    public abstract boolean fallsThrough();

    /**
     * Returns true if execution after this statement does not necessarily continue at the following statement. GotoStmt and
     * IfStmt will both return true.
     */
    public abstract boolean branches();
}
