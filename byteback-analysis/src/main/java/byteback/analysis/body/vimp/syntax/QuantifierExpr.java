package byteback.analysis.body.vimp.syntax;

import soot.Local;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Jimple;
import soot.util.Chain;
import soot.util.HashChain;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class QuantifierExpr implements LogicExpr {

    private Chain<Local> bindings;

    private final ValueBox valueBox;

    public QuantifierExpr(final Chain<Local> bindings, final ValueBox valueBox) {
        this.valueBox = valueBox;
        setBindings(bindings);
    }

    public QuantifierExpr(final Chain<Local> bindings, final Value value) {
        this(bindings, Jimple.v().newImmediateBox(value));
    }

    public Value getValue() {
        return valueBox.getValue();
    }

    public void setValue(final Value value) {
        this.valueBox.setValue(value);
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

    protected abstract String getSymbol();

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
        return Collections.singletonList(valueBox);
    }

    @Override
    public boolean equivTo(final Object object) {
        return object instanceof QuantifierExpr quantifierExpr && getValue().equivTo(quantifierExpr.getValue());
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

}
