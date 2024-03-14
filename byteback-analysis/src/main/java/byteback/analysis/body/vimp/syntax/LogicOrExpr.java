package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.jimple.syntax.OrExpr;
import soot.util.Switch;

public class LogicOrExpr extends LogicBinopExpr implements OrExpr {

    public LogicOrExpr(final Value op1, final Value op2) {
        super(op1, op2);
    }

    public LogicOrExpr(final ValueBox op1box, final ValueBox op2box) {
        super(op1box, op2box);
    }

    public String getSymbol() {
        return " ∨ ";
    }
}
