package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.Local;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.common.syntax.Chain;
import byteback.analysis.common.syntax.HashChain;
import soot.UnitPrinter;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.jimple.syntax.Jimple;
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
