package byteback.analysis.vimp;

import byteback.analysis.LogicExprVisitor;
import byteback.analysis.Vimp;
import soot.Value;
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
	public void apply(final Switch sw) {
		if (sw instanceof LogicExprVisitor<?> visitor) {
			visitor.caseLogicIffExpr(this);
		}
	}

	@Override
	public int getPrecedence() {
		return 500;
	}

}
