package byteback.analysis.body.jimple.syntax.stmt;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.jimple.syntax.expr.ImmediateBox;
import byteback.analysis.body.common.syntax.expr.Value;

public class EnterMonitorStmt extends OpStmt {

    public EnterMonitorStmt(final Value op) {
        this(new ImmediateBox(op));
    }

    protected EnterMonitorStmt(final ValueBox opBox) {
        super(opBox);
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
