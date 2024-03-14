package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.Value;

public class GeExpr extends IntBinopExpr {

    public GeExpr(Value op1, Value op2) {
        super(new ImmediateBox(op1), new ImmediateBox(op2));
    }

    @Override
    public final String getSymbol() {
        return " >= ";
    }
}
