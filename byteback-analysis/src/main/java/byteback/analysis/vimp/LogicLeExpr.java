package byteback.analysis.vimp;

import byteback.analysis.Vimp;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ExprSwitch;
import soot.jimple.LeExpr;
import soot.util.Switch;

public class LogicLeExpr extends AbstractLogicBinopExpr implements LeExpr {

	public LogicLeExpr(final Value op1, final Value op2) {
		super(op1, op2);
	}

	public LogicLeExpr(final ValueBox op1box, final ValueBox op2box) {
		super(op1box, op2box);
	}

	@Override
	public String getSymbol() {
		return " <= ";
	}

	@Override
	public LogicLeExpr clone() {
		return new LogicLeExpr(Vimp.cloneIfNecessary(getOp1()), Vimp.cloneIfNecessary(getOp2()));
	}

	@Override
	public void apply(final Switch sw) {
		((ExprSwitch) sw).caseLeExpr(this);
	}

	@Override
	public int getPrecedence() {
		return 600;
	}

}
