package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.expr.Value;

public class LogicImpliesExpr extends LogicBinopExpr {

    public LogicImpliesExpr(final Value op1, final Value op2) {
        super(op1, op2);
    }

    @Override
    public String getSymbol() {
        return " → ";
    }
}
