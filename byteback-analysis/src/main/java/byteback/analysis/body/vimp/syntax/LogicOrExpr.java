package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import soot.Value;
import soot.ValueBox;
import soot.jimple.OrExpr;
import soot.util.Switch;

public class LogicOrExpr extends AbstractLogicBinopExpr implements OrExpr {

    public LogicOrExpr(final Value op1, final Value op2) {
        super(op1, op2);
    }

    public LogicOrExpr(final ValueBox op1box, final ValueBox op2box) {
        super(op1box, op2box);
    }

    public String getSymbol() {
        return " ∨ ";
    }

    public LogicOrExpr clone() {
        return new LogicOrExpr(Vimp.cloneIfNecessary(getOp1()), Vimp.cloneIfNecessary(getOp2()));
    }

    @Override
    public void apply(final Switch visitor) {
        if (visitor instanceof VimpValueSwitch<?> vimpValueSwitch) {
            vimpValueSwitch.caseLogicOrExpr(this);
        }
    }

    @Override
    public int getPrecedence() {
        return 500;
    }

}
