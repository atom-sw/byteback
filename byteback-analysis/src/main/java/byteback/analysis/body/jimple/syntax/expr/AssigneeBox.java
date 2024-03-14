package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.common.syntax.Immediate;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.jimple.syntax.expr.Expr;

public class AssigneeBox extends ValueBox {

    public AssigneeBox(Value value) {
        setValue(value);
    }

    @Override
    public boolean canContainValue(Value value) {
        return value instanceof Immediate || value instanceof ConcreteRef || value instanceof Expr;
    }
}
