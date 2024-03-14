package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.common.syntax.expr.Value;

public class ConditionExprBox extends ValueBox {

    public ConditionExprBox(final Value value) {
        setValue(value);
    }

    @Override
    public boolean canContainValue(Value value) {
        return value instanceof ConditionExpr;
    }
}
