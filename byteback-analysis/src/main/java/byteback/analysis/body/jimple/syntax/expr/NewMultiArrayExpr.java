package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.model.syntax.type.ArrayType;
import byteback.analysis.model.syntax.type.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class NewMultiArrayExpr implements Expr {

    protected ArrayType baseType;
    protected final ValueBox[] sizeBoxes;

    protected NewMultiArrayExpr(final ArrayType type, final ValueBox[] sizeBoxes) {
        this.baseType = type;
        this.sizeBoxes = sizeBoxes;
    }

    @Override
    public abstract Object clone();

    @Override
    public boolean equivTo(final Object object) {
        if (object instanceof NewMultiArrayExpr newMultiArrayExpr) {
            return baseType.equals(newMultiArrayExpr.baseType)
                    && (this.sizeBoxes.length == newMultiArrayExpr.sizeBoxes.length);
        }

        return false;
    }

    /**
     * Returns a hash code for this object, consistent with structural equality.
     */
    @Override
    public int equivHashCode() {
        return baseType.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("newarray (");

        buf.append(baseType.baseType).append(')');
        for (ValueBox element : sizeBoxes) {
            buf.append('[').append(element.getValue().toString()).append(']');
        }
        for (int i = 0, e = baseType.dimensions - sizeBoxes.length; i < e; i++) {
            buf.append("[]");
        }

        return buf.toString();
    }

    public ArrayType getBaseType() {
        return baseType;
    }

    public void setBaseType(final ArrayType baseType) {
        this.baseType = baseType;
    }

    public ValueBox getSizeBox(int index) {
        return sizeBoxes[index];
    }

    public int getSizeCount() {
        return sizeBoxes.length;
    }

    public Value getSize(int index) {
        return sizeBoxes[index].getValue();
    }

    public List<Value> getSizes() {
        final ValueBox[] boxes = sizeBoxes;
        final var toReturn = new ArrayList<Value>(boxes.length);

        for (ValueBox element : boxes) {
            toReturn.add(element.getValue());
        }

        return toReturn;
    }

    public void setSize(int index, Value size) {
        sizeBoxes[index].setValue(size);
    }

    @Override
    public final List<ValueBox> getUseBoxes() {
        List<ValueBox> list = new ArrayList<ValueBox>();
        Collections.addAll(list, sizeBoxes);

        for (final ValueBox element : sizeBoxes) {
            list.addAll(element.getValue().getUseBoxes());
        }

        return list;
    }

    @Override
    public Type getType() {
        return baseType;
    }
}
