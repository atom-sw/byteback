package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.Local;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.model.syntax.type.NullType;
import byteback.analysis.model.syntax.type.Type;
import byteback.analysis.model.syntax.type.UnknownType;

import java.util.ArrayList;
import java.util.List;

public class ArrayRef implements Ref {

    protected final ValueBox baseBox;
    protected final ValueBox indexBox;

    public ArrayRef(Value base, Value index) {
        this(new LocalBox(base), new ImmediateBox(index));
    }

    protected ArrayRef(final ValueBox baseBox, final ValueBox indexBox) {
        this.baseBox = baseBox;
        this.indexBox = indexBox;
    }

    @Override
    public boolean equivTo(Object o) {
        if (o instanceof ArrayRef arrayRef) {
            return this.getBase().equivTo(arrayRef.getBase()) && this.getIndex().equivTo(arrayRef.getIndex());
        }

        return false;
    }

    /** Returns a hash code for this object, consistent with structural equality. */
    @Override
    public int equivHashCode() {
        return getBase().equivHashCode() * 101 + getIndex().equivHashCode() + 17;
    }

    @Override
    public String toString() {
        return baseBox.getValue().toString() + "[" + indexBox.getValue().toString() + "]";
    }

    public Value getBase() {
        return baseBox.getValue();
    }

    public void setBase(Local base) {
        baseBox.setValue(base);
    }

    public ValueBox getBaseBox() {
        return baseBox;
    }

    public Value getIndex() {
        return indexBox.getValue();
    }

    public void setIndex(Value index) {
        indexBox.setValue(index);
    }

    public ValueBox getIndexBox() {
        return indexBox;
    }

    @Override
    public List<ValueBox> getUseBoxes() {
        List<ValueBox> useBoxes = new ArrayList<>();

        useBoxes.addAll(baseBox.getValue().getUseBoxes());
        useBoxes.add(baseBox);

        useBoxes.addAll(indexBox.getValue().getUseBoxes());
        useBoxes.add(indexBox);

        return useBoxes;
    }

    @Override
    public Type getType() {
        Type type = baseBox.getValue().getType();

        if (UnknownType.v().equals(type)) {
            return UnknownType.v();
        } else if (NullType.v().equals(type)) {
            return NullType.v();
        } else {
            return type;
        }
    }
}