package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.model.syntax.type.BooleanType;
import byteback.analysis.model.syntax.type.Type;

import java.util.ArrayList;
import java.util.List;

public class InstanceOfExpr implements Expr {

    protected final ValueBox opBox;
    protected final Type checkType;

    protected InstanceOfExpr(final ValueBox opBox, final Type checkType) {
        this.opBox = opBox;
        this.checkType = checkType;
    }

    public InstanceOfExpr(final Value op, final Type checkType) {
        this(new ImmediateBox(op), checkType);
    }

    @Override
    public String toString() {
        return opBox.getValue().toString() + " instanceof " + checkType.toString();
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

    @Override
    public Type getType() {
        return BooleanType.v();
    }

    public Type getCheckType() {
        return checkType;
    }
}
