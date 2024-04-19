package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.*;
import soot.jimple.internal.RValueBox;

import java.util.ArrayList;
import java.util.List;

public class NestedExpr implements Immediate, DefaultCaseValue {

    private final RValueBox valueBox;

    public NestedExpr(final RValueBox valueBox) {
        this.valueBox = valueBox;
    }

    public NestedExpr(final Value value) {
        this(new RValueBox(value));
    }

    public Value getValue() {
        return valueBox.getValue();
    }

    @Override
    public List<ValueBox> getUseBoxes() {
        final Value value = valueBox.getValue();
        final var useBoxes = new ArrayList<ValueBox>(value.getUseBoxes());
        useBoxes.add(valueBox);

        return useBoxes;
    }

    @Override
    public Type getType() {
        final Value value = valueBox.getValue();
        return value.getType();
    }

    @Override
    public Object clone() {
        final Value value = valueBox.getValue();
        return new NestedExpr((Value) value.clone());
    }

    @Override
    public boolean equivTo(final Object object) {
        final Value value = valueBox.getValue();
        return value.equivTo(object);
    }

    @Override
    public int equivHashCode() {
        final Value value = valueBox.getValue();
        return value.equivHashCode();
    }

    @Override
    public void toString(final UnitPrinter printer) {
        final Value value = valueBox.getValue();
        printer.literal("(");
        value.toString(printer);
        printer.literal(")");
    }

}