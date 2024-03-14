package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.Value;

public class GtExpr extends IntBinopExpr {

    public GtExpr(final Value op1, final Value op2) {
        super(new ImmediateBox(op1), new ImmediateBox(op2));
    }

    @Override
    public final String getSymbol() {
        return " > ";
    }
}
