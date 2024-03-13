package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.jimple.syntax.EqExpr;
import byteback.analysis.body.jimple.syntax.ExprSwitch;
import soot.util.Switch;

public class LogicEqExpr extends AbstractLogicBinopExpr implements EqExpr {

    public LogicEqExpr(final Value op1, final Value op2) {
        super(op1, op2);
    }

    public LogicEqExpr(final ValueBox op1box, final ValueBox op2box) {
        super(op1box, op2box);
    }

    @Override
    public String getSymbol() {
        return " == ";
    }

    @Override
    public LogicEqExpr clone() {
        return new LogicEqExpr(Vimp.cloneIfNecessary(getOp1()), Vimp.cloneIfNecessary(getOp2()));
    }

    @Override
    public void apply(final Switch visitor) {
        ((ExprSwitch) visitor).caseEqExpr(this);
    }

    @Override
    public int getPrecedence() {
        return 600;
    }

}
