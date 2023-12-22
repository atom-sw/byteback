package byteback.analysis.vimp;

import byteback.analysis.LogicExprVisitor;
import byteback.analysis.Vimp;
import soot.Value;
import soot.util.Switch;

public class LogicImpliesExpr extends AbstractLogicBinopExpr {

	public LogicImpliesExpr(final Value op1, final Value op2) {
		super(op1, op2);
	}

	public String getSymbol() {
		return " → ";
	}

	public LogicImpliesExpr clone() {
		return new LogicImpliesExpr(Vimp.cloneIfNecessary(getOp1()), Vimp.cloneIfNecessary(getOp2()));
	}

	@Override
	public void apply(final Switch sw) {
		((LogicExprVisitor<?>) sw).caseLogicImpliesExpr(this);
	}

	@Override
	public int getPrecedence() {
		return 500;
	}

}
