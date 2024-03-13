package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.jimple.syntax.Unit;
import byteback.analysis.body.common.syntax.Value;
import soot.grimp.Grimp;
import byteback.analysis.body.jimple.syntax.internal.JIfStmt;

public class LogicIfStmt extends JIfStmt {

    public LogicIfStmt(final Value condition, final Unit target) {
        super(Grimp.v().newArgBox(condition), Grimp.v().newStmtBox(target));
    }

}
