package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.model.syntax.type.IntType;
import byteback.analysis.model.syntax.type.Type;

public abstract class IntBinopExpr extends BinopExpr {

    public IntBinopExpr(final ValueBox op1Box, final ValueBox op2Box) {
        super(op1Box, op2Box);
    }

    @Override
    public Type getType() {
        return IntType.v();
    }
}
