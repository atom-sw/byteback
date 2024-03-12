package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AndExpr;
import soot.util.Switch;

public class LogicAndExpr extends AbstractLogicBinopExpr implements AndExpr {

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

    @Override
    public LogicAndExpr clone() {
        return new LogicAndExpr(Vimp.cloneIfNecessary(getOp1()), Vimp.cloneIfNecessary(getOp2()));
    }

    @Override
    public void apply(final Switch visitor) {
        if (visitor instanceof VimpValueSwitch<?> vimpValueSwitch) {
            vimpValueSwitch.caseLogicAndExpr(this);
        }
    }

    @Override
    public int getPrecedence() {
        return 500;
    }

}
