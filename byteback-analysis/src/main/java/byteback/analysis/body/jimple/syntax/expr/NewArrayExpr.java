package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.model.syntax.type.ArrayType;
import byteback.analysis.model.syntax.type.Type;

import java.util.ArrayList;
import java.util.List;

public abstract class NewArrayExpr implements Expr {

    protected Type baseType;
    protected final ValueBox sizeBox;

    protected NewArrayExpr(final Type type, final ValueBox sizeBox) {
        this.baseType = type;
        this.sizeBox = sizeBox;
    }

    @Override
    public abstract Object clone();

    @Override
    public boolean equivTo(Object o) {
        if (o instanceof NewArrayExpr ae) {
            return this.sizeBox.getValue().equivTo(ae.sizeBox.getValue()) && this.baseType.equals(ae.baseType);
        }

        return false;
    }

    /**
     * Returns a hash code for this object, consistent with structural equality.
     */
    @Override
    public int equivHashCode() {
        return sizeBox.getValue().equivHashCode() * 101 + baseType.hashCode() * 17;
    }

    @Override
    public String toString() {
        return "newarray (" + getBaseTypeString() + ")[" + sizeBox.getValue().toString() + ']';
    }

    private String getBaseTypeString() {
        return baseType.toString();
    }

    public Type getBaseType() {
        return baseType;
    }

    public void setBaseType(final Type type) {
        baseType = type;
    }

    public ValueBox getSizeBox() {
        return sizeBox;
    }

    public Value getSize() {
        return sizeBox.getValue();
    }

    public void setSize(final Value size) {
        sizeBox.setValue(size);
    }

    @Override
    public final List<ValueBox> getUseBoxes() {
        final var useBoxes = new ArrayList<>(sizeBox.getValue().getUseBoxes());
        useBoxes.add(sizeBox);

        return useBoxes;
    }

    @Override
    public Type getType() {
        if (baseType instanceof ArrayType base) {
            return new ArrayType(base.baseType, base.dimensions + 1);
        } else {
            return new ArrayType(baseType, 1);
        }
    }
}
