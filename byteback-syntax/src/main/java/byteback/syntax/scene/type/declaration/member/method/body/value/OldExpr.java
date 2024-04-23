package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.syntax.scene.type.declaration.member.method.body.unit.printer.InlineUnitPrinter;
import soot.Type;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.grimp.internal.ObjExprBox;
import soot.jimple.ConcreteRef;
import soot.jimple.internal.AbstractUnopExpr;

/**
 * An expression to access the pre-state of a method.
 *
 * @author paganma
 */
public class OldExpr extends AbstractUnopExpr implements ConcreteRef, DefaultCaseValue {

    public OldExpr(final Value value) {
        super(new ObjExprBox(value));
    }

    public OldExpr(final ValueBox valueBox) {
        super(valueBox);
    }

    @Override
    public void toString(final UnitPrinter printer) {
        printer.literal("old");
        printer.literal("(");
        getOp().toString(printer);
        printer.literal(")");
    }

    @Override
    public Object clone() {
        return new OldExpr(getOp());
    }

    @Override
    public Type getType() {
        return getOp().getType();
    }

    @Override
    public boolean equivTo(final Object object) {
        return object instanceof final OldExpr oldExpr
                && oldExpr.getOp().equivTo(getOp());
    }

    @Override
    public int equivHashCode() {
        return getOp().equivHashCode() * 101 + 17 ^ "old".hashCode();
    }

    @Override
    public String toString() {
        final var printer = new InlineUnitPrinter();
        toString(printer);

        return printer.toString();
    }

}
