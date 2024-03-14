package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.jimple.syntax.AndExpr;
import soot.util.Switch;

public class LogicAndExpr extends LogicBinopExpr implements AndExpr {

    public LogicAndExpr(final Value op1, final Value op2) {
        super(op1, op2);
    }

    public LogicAndExpr(final ValueBox op1box, final ValueBox op2box) {
        super(op1box, op2box);
    }

    @Override
    public String getSymbol() {
        return " ∧ ";
    }
}
