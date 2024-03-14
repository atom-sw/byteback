package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.jimple.syntax.stmt.UnopExpr;
import byteback.analysis.model.syntax.type.IntType;
import byteback.analysis.model.syntax.type.Type;

public class LengthExpr extends UnopExpr {

    public LengthExpr(final ValueBox opBox) {
        super(opBox);
    }

    public LengthExpr(final Value op) {
        this(new LocalBox(op));
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
