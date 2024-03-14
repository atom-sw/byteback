package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.common.syntax.expr.Value;

public class InvokeExprBox extends ValueBox {

    public InvokeExprBox(Value value) {
        setValue(value);
    }

    @Override
    public boolean canContainValue(Value value) {
        return value instanceof InvokeExpr;
    }
}
