package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.vimp.Vimp;
import soot.UnitPrinter;
import byteback.analysis.body.common.syntax.Value;
import soot.grimp.Precedence;
import byteback.analysis.body.jimple.syntax.expr.BinopExpr;

public abstract class LogicBinopExpr extends BinopExpr implements LogicExpr {

    public LogicBinopExpr(final Value op1, final Value op2) {
        super(Vimp.v().newArgBox(op1), Vimp.v().newArgBox(op2));
    }

    public LogicBinopExpr(final ValueBox op1box, final ValueBox op2box) {
        super(op1box, op2box);
    }

    @Override
    public abstract String getSymbol();
}
