package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import byteback.analysis.body.common.syntax.Value;
import soot.util.Switch;

public class LogicIffExpr extends AbstractLogicBinopExpr {

    public LogicIffExpr(final Value op1, final Value op2) {
        super(op1, op2);
    }

    @Override
    public String getSymbol() {
        return " ↔ ";
    }

    @Override
    public LogicIffExpr clone() {
        return new LogicIffExpr(Vimp.cloneIfNecessary(getOp1()), Vimp.cloneIfNecessary(getOp2()));
    }

    @Override
    public void apply(final Switch visitor) {
        if (visitor instanceof VimpValueSwitch<?> vimpValueSwitch) {
            vimpValueSwitch.caseLogicIffExpr(this);
        }
    }

    @Override
    public int getPrecedence() {
        return 500;
    }

}
