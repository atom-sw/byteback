package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.Local;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;

public class LocalBox extends ValueBox {

    public LocalBox(Value value) {
        setValue(value);
    }

    @Override
    public boolean canContainValue(Value value) {
        return value instanceof Local;
    }
}