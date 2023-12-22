package byteback.analysis.vimp;

import byteback.analysis.Vimp;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ExprSwitch;
import soot.jimple.NeExpr;
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
	public void apply(final Switch sw) {
		((ExprSwitch) sw).caseNeExpr(this);
	}

	@Override
	public int getPrecedence() {
		return 600;
	}

}
