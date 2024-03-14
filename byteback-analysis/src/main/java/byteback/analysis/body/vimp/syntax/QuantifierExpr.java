package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.jimple.syntax.expr.ImmediateBox;
import byteback.analysis.body.jimple.syntax.expr.Local;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.common.syntax.Chain;
import byteback.analysis.body.common.syntax.expr.Value;

import java.util.Collections;
import java.util.List;

public abstract class QuantifierExpr implements LogicExpr {

    private Chain<Local> bindings;

    private final ValueBox valueBox;

    public QuantifierExpr(final Chain<Local> bindings, final ValueBox valueBox) {
        this.valueBox = valueBox;
        setBindings(bindings);
    }

    public QuantifierExpr(final Chain<Local> bindings, final Value value) {
        this(bindings, new ImmediateBox(value));
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

    protected abstract String getSymbol();
    @Override
    public List<ValueBox> getUseBoxes() {
        return Collections.singletonList(valueBox);
    }
}
