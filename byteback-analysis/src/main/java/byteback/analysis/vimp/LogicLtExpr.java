package byteback.analysis.vimp;

import byteback.analysis.Vimp;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ExprSwitch;
import soot.jimple.LtExpr;
import soot.util.Switch;

public class LogicLtExpr extends AbstractLogicBinopExpr implements LtExpr {

	public LogicLtExpr(final Value op1, final Value op2) {
		super(op1, op2);
	}

	public LogicLtExpr(final ValueBox op1box, final ValueBox op2box) {
		super(op1box, op2box);
	}

	@Override
	public String getSymbol() {
		return " < ";
	}

	@Override
	public LogicLtExpr clone() {
		return new LogicLtExpr(Vimp.cloneIfNecessary(getOp1()), Vimp.cloneIfNecessary(getOp2()));
	}

	@Override
	public void apply(final Switch sw) {
		((ExprSwitch) sw).caseLtExpr(this);
	}

	@Override
	public int getPrecedence() {
		return 600;
	}

}
