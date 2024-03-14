package byteback.analysis.body.jimple.syntax.stmt;

import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.jimple.syntax.expr.ImmediateBox;

public class ThrowStmt extends OpStmt {

    public ThrowStmt(Value op) {
        this(new ImmediateBox(op));
    }

    protected ThrowStmt(final ValueBox opBox) {
        super(opBox);
    }

    @Override
    public String toString() {
        return "throw " + opBox.getValue().toString();
    }

    @Override
    public boolean fallsThrough() {
        return false;
    }

    @Override
    public boolean branches() {
        return false;
    }
}
