package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import byteback.analysis.body.common.syntax.Value;
import soot.util.Switch;

public class LogicImpliesExpr extends LogicBinopExpr {

    public LogicImpliesExpr(final Value op1, final Value op2) {
        super(op1, op2);
    }

    @Override
    public String getSymbol() {
        return " → ";
    }

    @Override
    public LogicImpliesExpr clone() {
        return new LogicImpliesExpr(Vimp.cloneIfNecessary(getOp1()), Vimp.cloneIfNecessary(getOp2()));
    }
}
