package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.jimple.syntax.stmt.UnopExpr;
import byteback.analysis.model.syntax.type.IntType;
import byteback.analysis.model.syntax.type.Type;

public abstract class LengthExpr extends UnopExpr {

    protected LengthExpr(final ValueBox opBox) {
        super(opBox);
    }

    @Override
    public String toString() {
        return "lengthof " + opBox.getValue().toString();
    }

    @Override
    public Type getType() {
        return IntType.v();
    }
}
