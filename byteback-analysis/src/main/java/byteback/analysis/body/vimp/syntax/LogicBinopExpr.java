package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.jimple.syntax.expr.BinopExpr;
import byteback.analysis.body.jimple.syntax.expr.ImmediateBox;

public abstract class LogicBinopExpr extends BinopExpr implements LogicExpr {

    public LogicBinopExpr(final Value op1, final Value op2) {
        super(new ImmediateBox(op1), new ImmediateBox(op2));
    }

    public LogicBinopExpr(final ValueBox op1box, final ValueBox op2box) {
        super(op1box, op2box);
    }

    @Override
    public abstract String getSymbol();
}
