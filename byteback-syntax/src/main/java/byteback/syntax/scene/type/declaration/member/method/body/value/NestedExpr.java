package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.syntax.scene.type.declaration.member.method.body.value.box.AssignedValueBox;
import soot.*;
import soot.jimple.Jimple;
import soot.jimple.internal.RValueBox;

import java.util.ArrayList;
import java.util.List;

public class NestedExpr implements Immediate, DefaultCaseValue {

    private final AssignedValueBox valueBox;

    public NestedExpr(final AssignedValueBox valueBox) {
        this.valueBox = valueBox;
    }

    public NestedExpr(final Value value) {
        this(new AssignedValueBox(value));
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
        return new NestedExpr(Jimple.cloneIfNecessary(getValue()));
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
        printer.literal("(");
        getValue().toString(printer);
        printer.literal(")");
    }

}
