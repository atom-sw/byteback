package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.model.syntax.type.Type;

import java.util.ArrayList;
import java.util.List;

public abstract class CastExpr implements Expr {

    protected final ValueBox opBox;

    protected Type type;

    public CastExpr(final Value op, final Type type) {
        this(new ImmediateBox(op), type);
    }

    @Override
    public abstract Object clone();

    protected CastExpr(final ValueBox opBox, final Type type) {
        this.opBox = opBox;
        this.type = type;
    }

    @Override
    public boolean equivTo(Object o) {
        if (o instanceof CastExpr ace) {
            return this.opBox.getValue().equivTo(ace.opBox.getValue()) && this.type.equals(ace.type);
        }
        return false;
    }

    /**
     * Returns a hash code for this object, consistent with structural equality.
     */
    @Override
    public int equivHashCode() {
        return opBox.getValue().equivHashCode() * 101 + type.hashCode() + 17;
    }

    @Override
    public String toString() {
        return "(" + type.toString() + ") " + opBox.getValue().toString();
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

    public final List<ValueBox> getUseBoxes() {
        final var list = new ArrayList<ValueBox>(opBox.getValue().getUseBoxes());
        list.add(opBox);
        return list;
    }

    public Type getCastType() {
        return type;
    }

    public void setCastType(final Type castType) {
        this.type = castType;
    }

    public Type getType() {
        return type;
    }
}
