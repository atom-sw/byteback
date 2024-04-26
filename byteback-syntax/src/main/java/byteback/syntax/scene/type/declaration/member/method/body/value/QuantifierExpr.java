package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import soot.*;
import soot.grimp.Grimp;
import soot.jimple.Expr;
import soot.jimple.Jimple;
import soot.util.Chain;
import soot.util.HashChain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Base implementation for a quantifier expression.
 *
 * @author paganma
 */
public abstract class QuantifierExpr implements Expr {

    private Chain<Local> bindings;

    private final ValueBox conditionBox;

    public QuantifierExpr(final Chain<Local> bindings, final ValueBox conditionBox) {
        this.conditionBox = conditionBox;
        setBindings(bindings);
    }

    /**
     * Constructor for a quantifier expression.
     *
     * @param bindings  The bindings of this quantification.
     * @param condition The actual expression (which may refer to the above bindings).
     */
    public QuantifierExpr(final Chain<Local> bindings, final Value condition) {
        this(bindings, Grimp.v().newExprBox(condition));
    }

    public Value getValue() {
        return conditionBox.getValue();
    }

    public void setValue(final Value value) {
        this.conditionBox.setValue(value);
    }

    public Chain<Local> getBindings() {
        return bindings;
    }

    public final void setBindings(final Chain<Local> bindings) {
        if (bindings.isEmpty()) {
            throw new IllegalArgumentException("A Quantifier must have at least one free local");
        }

        this.bindings = bindings;
    }

    protected Chain<Local> cloneBindings() {
        final Chain<Local> locals = new HashChain<>();

        for (Local local : getBindings()) {
            locals.add((Local) local.clone());
        }

        return locals;
    }

    public abstract String getSymbol();

    @Override
    public void toString(final UnitPrinter printer) {
        final Iterator<Local> freeIt = bindings.iterator();
        printer.literal("(");
        printer.literal(getSymbol());
        printer.literal(" ");

        while (freeIt.hasNext()) {
            final Local local = freeIt.next();
            printer.type(local.getType());
            printer.literal(" ");
            local.toString(printer);

            if (freeIt.hasNext()) {
                printer.literal(", ");
            }
        }

        printer.literal(" :: ");
        getValue().toString(printer);
        printer.literal(")");
    }

    @Override
    public List<ValueBox> getUseBoxes() {
        final var useBoxes = new ArrayList<ValueBox>();
        useBoxes.add(conditionBox);
        useBoxes.addAll(conditionBox.getValue().getUseBoxes());

        return useBoxes;
    }

    @Override
    public boolean equivTo(final Object object) {
        return object instanceof final QuantifierExpr quantifierExpr
                && getValue().equivTo(quantifierExpr.getValue());
    }

    @Override
    public int equivHashCode() {
        int hashCode = 17 ^ getSymbol().hashCode();

        for (Local local : bindings) {
            hashCode += local.equivHashCode();
        }

        return hashCode + (getValue().equivHashCode() * 101);
    }

    @Override
    public abstract Object clone();

    @Override
    public Type getType() {
        return BooleanType.v();
    }

}
