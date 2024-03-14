package byteback.analysis.body.jimple.syntax.stmt;

import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.jimple.syntax.expr.Expr;

import java.util.ArrayList;
import java.util.List;

public abstract class UnopExpr implements Expr {

    protected final ValueBox opBox;

    protected UnopExpr(final ValueBox opBox) {
        this.opBox = opBox;
    }

    public Value getOp() {
        return opBox.getValue();
    }

    public void setOp(final Value op) {
        opBox.setValue(op);
    }

    public ValueBox getOpBox() {
        return opBox;
    }

    @Override
    public final List<ValueBox> getUseBoxes() {
        List<ValueBox> list = new ArrayList<ValueBox>(opBox.getValue().getUseBoxes());
        list.add(opBox);
        return list;
    }
}
