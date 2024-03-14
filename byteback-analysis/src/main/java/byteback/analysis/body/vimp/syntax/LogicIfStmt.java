package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.stmt.StmtBox;
import byteback.analysis.body.common.syntax.stmt.Unit;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.jimple.syntax.expr.ImmediateBox;
import byteback.analysis.body.jimple.syntax.stmt.IfStmt;

public class LogicIfStmt extends IfStmt {

    public LogicIfStmt(final Value condition, final Unit target) {
        super(new ImmediateBox(condition), new StmtBox(target));
    }

}
