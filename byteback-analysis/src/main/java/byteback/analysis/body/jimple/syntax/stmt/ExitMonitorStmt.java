package byteback.analysis.body.jimple.syntax.stmt;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.jimple.syntax.expr.ImmediateBox;
import byteback.analysis.body.common.syntax.expr.Value;

public class ExitMonitorStmt extends OpStmt {

    public ExitMonitorStmt(final Value op) {
        this(new ImmediateBox(op));
    }

    protected ExitMonitorStmt(ValueBox opBox) {
        super(opBox);
    }

    @Override
    public String toString() {
        return "exitmonitor " + opBox.getValue().toString();
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
