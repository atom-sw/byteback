package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.jimple.syntax.expr.BinopExpr;
import byteback.analysis.model.syntax.type.*;

public abstract class IntLongBinopExpr extends BinopExpr {

    protected IntLongBinopExpr(ValueBox op1Box, ValueBox op2Box) {
        super(op1Box, op2Box);
    }

    public static boolean isIntLikeType(Type t) {
        return IntType.v().equals(t) || ByteType.v().equals(t) || ShortType.v().equals(t) || CharType.v().equals(t)
                || BooleanType.v().equals(t);
    }

    @Override
    public Type getType() {
        return getType(BinopExpr.BinopExprEnum.ABASTRACT_INT_LONG_BINOP_EXPR);
    }
}
