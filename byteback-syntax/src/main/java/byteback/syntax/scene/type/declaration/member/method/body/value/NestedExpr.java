package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.*;

import java.util.List;

public class NestedExpr implements Immediate, DefaultCaseValue {

    private final Value value;

    public NestedExpr(final Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public List<ValueBox> getUseBoxes() {
        return value.getUseBoxes();
    }

    @Override
    public Type getType() {
        return value.getType();
    }

    @Override
    public Object clone() {
        return new NestedExpr((Value) value.clone());
    }

    @Override
    public boolean equivTo(final Object object) {
        return value.equivTo(object);
    }

    @Override
    public int equivHashCode() {
        return value.equivHashCode();
    }

    @Override
    public void toString(final UnitPrinter printer) {
        printer.literal("(");
        value.toString(printer);
        printer.literal(")");
    }

}
