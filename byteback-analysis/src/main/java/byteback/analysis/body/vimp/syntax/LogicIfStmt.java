package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.jimple.syntax.Unit;
import byteback.analysis.body.common.syntax.Value;
import soot.grimp.Grimp;
import byteback.analysis.body.jimple.syntax.stmt.IfStmt;

public class LogicIfStmt extends IfStmt {

    public LogicIfStmt(final Value condition, final Unit target) {
        super(Grimp.v().newArgBox(condition), Grimp.v().newStmtBox(target));
    }

}
