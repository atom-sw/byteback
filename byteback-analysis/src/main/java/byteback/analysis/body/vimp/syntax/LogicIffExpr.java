package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.expr.Value;

public class LogicIffExpr extends LogicBinopExpr {

    public LogicIffExpr(final Value op1, final Value op2) {
        super(op1, op2);
    }

    @Override
    public String getSymbol() {
        return " ↔ ";
    }
}
