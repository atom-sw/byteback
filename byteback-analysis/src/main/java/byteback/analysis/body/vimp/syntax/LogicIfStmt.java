package byteback.analysis.body.vimp.syntax;

import soot.Unit;
import soot.Value;
import soot.grimp.Grimp;
import soot.jimple.internal.JIfStmt;

public class LogicIfStmt extends JIfStmt {

    public LogicIfStmt(final Value condition, final Unit target) {
        super(Grimp.v().newArgBox(condition), Grimp.v().newStmtBox(target));
    }

}
