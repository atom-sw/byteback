package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.model.syntax.type.BooleanType;
import byteback.analysis.model.syntax.type.Type;

import java.util.ArrayList;
import java.util.List;

public abstract class InstanceOfExpr implements Expr {

    protected final ValueBox opBox;
    protected Type checkType;

    protected InstanceOfExpr(ValueBox opBox, Type checkType) {
        this.opBox = opBox;
        this.checkType = checkType;
    }

    @Override
    public abstract Object clone();

    @Override
    public boolean equivTo(Object o) {
        if (o instanceof InstanceOfExpr aie) {
            return this.opBox.getValue().equivTo(aie.opBox.getValue()) && this.checkType.equals(aie.checkType);
        }
        return false;
    }

    @Override
    public int equivHashCode() {
        return opBox.getValue().equivHashCode() * 101 + checkType.hashCode() * 17;
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

    public void setCheckType(final Type checkType) {
        this.checkType = checkType;
    }
}
