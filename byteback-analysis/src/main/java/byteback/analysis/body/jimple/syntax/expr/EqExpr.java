package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.jimple.syntax.expr.ImmediateBox;
import byteback.analysis.body.jimple.syntax.expr.IntBinopExpr;
import byteback.analysis.body.common.syntax.Value;

public class EqExpr extends IntBinopExpr {

    public EqExpr(Value op1, Value op2) {
        super(new ImmediateBox(op1), new ImmediateBox(op2));
    }

    @Override
    public final String getSymbol() {
        return " == ";
    }
}
