package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.common.syntax.Immediate;
import byteback.analysis.body.common.syntax.Value;

public class ImmediateBox extends ValueBox {

    public ImmediateBox(Value value) {
        setValue(value);
    }

    @Override
    public boolean canContainValue(Value value) {
        return value instanceof Immediate;
    }
}
