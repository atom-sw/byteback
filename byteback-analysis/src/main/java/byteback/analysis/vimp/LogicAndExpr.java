package byteback.analysis.vimp;

import byteback.analysis.LogicExprVisitor;
import byteback.analysis.Vimp;
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
	public void apply(final Switch sw) {
		if (sw instanceof LogicExprVisitor<?> visitor) {
			visitor.caseLogicAndExpr(this);
		}
	}

	@Override
	public int getPrecedence() {
		return 500;
	}

}
