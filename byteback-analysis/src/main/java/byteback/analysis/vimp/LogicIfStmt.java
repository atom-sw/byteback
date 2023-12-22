package byteback.analysis.vimp;

import soot.Unit;
import soot.Value;
import soot.grimp.Grimp;
import soot.jimple.internal.JIfStmt;

public class LogicIfStmt extends JIfStmt {

	public LogicIfStmt(final Value value, final Unit target) {
		super(Grimp.v().newArgBox(value), Grimp.v().newStmtBox(target));
	}

}
