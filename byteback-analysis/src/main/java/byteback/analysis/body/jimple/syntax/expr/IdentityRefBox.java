package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.common.syntax.Value;

public class IdentityRefBox extends ValueBox {

    public IdentityRefBox(final Value value) {
        setValue(value);
    }

    @Override
    public boolean canContainValue(final Value value) {
        return value instanceof IdentityRef;
    }
}
