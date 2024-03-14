package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.model.syntax.type.NullType;
import byteback.analysis.model.syntax.type.Type;
import byteback.analysis.model.syntax.type.UnknownType;

import java.util.ArrayList;
import java.util.List;

public class ArrayRef implements Ref {

    protected final ValueBox baseBox;
    protected final ValueBox indexBox;

    public ArrayRef(final Value base, final Value index) {
        this(new LocalBox(base), new ImmediateBox(index));
    }

    protected ArrayRef(final ValueBox baseBox, final ValueBox indexBox) {
        this.baseBox = baseBox;
        this.indexBox = indexBox;
    }

    @Override
    public String toString() {
        return baseBox.getValue().toString() + "[" + indexBox.getValue().toString() + "]";
    }

    public Value getBase() {
        return baseBox.getValue();
    }

    public ValueBox getBaseBox() {
        return baseBox;
    }

    public Value getIndex() {
        return indexBox.getValue();
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
        final Type type = baseBox.getValue().getType();

        if (UnknownType.v().equals(type)) {
            return UnknownType.v();
        } else if (NullType.v().equals(type)) {
            return NullType.v();
        } else {
            return type;
        }
    }
}