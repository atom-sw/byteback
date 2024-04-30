package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.syntax.scene.type.declaration.member.method.body.value.box.ConcreteRefBox;
import soot.*;
import soot.jimple.Jimple;
import soot.jimple.internal.AbstractUnopExpr;

public class ReadRefExpr extends AbstractUnopExpr implements DefaultCaseValue {

    protected ReadRefExpr(final ValueBox opBox) {
        super(opBox);
    }

    public ReadRefExpr(final Value value) {
        this(new ConcreteRefBox(value));
    }

    @Override
    public Type getType() {
        return getOp().getType();
    }

    @Override
    public Object clone() {
        return new ReadRefExpr(Jimple.cloneIfNecessary(getOp()));
    }

    @Override
    public void toString(final UnitPrinter printer) {
        printer.literal("read ");
        getOp().toString(printer);
    }

    @Override
    public boolean equivTo(final Object object) {
        return object instanceof final ReadRefExpr readRefExpr
                && readRefExpr.getOp().equivTo(getOp());
    }

    @Override
    public int equivHashCode() {
        return 413087 * getOp().equivHashCode();
    }

}
