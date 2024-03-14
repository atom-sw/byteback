package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.common.syntax.expr.Value;

public class VariableBox extends ValueBox {

    public VariableBox(Value value) {
        setValue(value);
    }

    @Override
    public boolean canContainValue(Value value) {
        return value instanceof Local || value instanceof ConcreteRef;
    }
}
