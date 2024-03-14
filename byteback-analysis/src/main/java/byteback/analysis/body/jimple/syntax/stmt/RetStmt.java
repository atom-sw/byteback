package byteback.analysis.body.jimple.syntax.stmt;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.common.syntax.stmt.Stmt;
import byteback.analysis.body.jimple.syntax.expr.LocalBox;
import byteback.analysis.body.common.syntax.expr.Value;

import java.util.ArrayList;
import java.util.List;

public class RetStmt extends Stmt {

    protected final ValueBox stmtAddressBox;

    public RetStmt(final Value stmtAddress) {
        this(new LocalBox(stmtAddress));
    }

    protected RetStmt(ValueBox stmtAddressBox) {
        this.stmtAddressBox = stmtAddressBox;

    }

    @Override
    public String toString() {
        return "return " + stmtAddressBox.getValue().toString();
    }

    @Override
    public List<ValueBox> getUseBoxes() {
        List<ValueBox> useBoxes = new ArrayList<ValueBox>(stmtAddressBox.getValue().getUseBoxes());
        useBoxes.add(stmtAddressBox);
        return useBoxes;
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
