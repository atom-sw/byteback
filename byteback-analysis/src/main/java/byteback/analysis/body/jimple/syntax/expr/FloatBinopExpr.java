package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.jimple.syntax.expr.BinopExpr;
import byteback.analysis.model.syntax.type.Type;

public abstract class FloatBinopExpr extends BinopExpr {

    protected FloatBinopExpr(final ValueBox op1Box, final ValueBox op2Box) {
        super(op1Box, op2Box);
    }

    @Override
    public Type getType() {
        return getType(BinopExpr.BinopExprEnum.ABSTRACT_FLOAT_BINOP_EXPR);
    }
}