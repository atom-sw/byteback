package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.common.syntax.expr.Immediate;
import byteback.analysis.body.common.syntax.expr.Value;

public class AssigneeBox extends ValueBox {

    public AssigneeBox(final Value value) {
        setValue(value);
    }

    @Override
    public boolean canContainValue(final Value value) {
        return value instanceof Immediate || value instanceof ConcreteRef || value instanceof Expr;
    }
}
