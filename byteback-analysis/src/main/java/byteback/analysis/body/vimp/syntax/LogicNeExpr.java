package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.jimple.syntax.ExprSwitch;
import byteback.analysis.body.jimple.syntax.NeExpr;
import soot.util.Switch;

public class LogicNeExpr extends AbstractLogicBinopExpr implements NeExpr {

    public LogicNeExpr(final Value op1, final Value op2) {
        super(op1, op2);
    }

    public LogicNeExpr(final ValueBox op1box, final ValueBox op2box) {
        super(op1box, op2box);
    }

    @Override
    public String getSymbol() {
        return " != ";
    }

    @Override
    public LogicNeExpr clone() {
        return new LogicNeExpr(Vimp.cloneIfNecessary(getOp1()), Vimp.cloneIfNecessary(getOp2()));
    }

    @Override
    public void apply(final Switch visitor) {
        ((ExprSwitch) visitor).caseNeExpr(this);
    }

    @Override
    public int getPrecedence() {
        return 600;
    }

}
