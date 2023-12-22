package byteback.analysis.vimp;

import byteback.analysis.LogicExprVisitor;
import byteback.analysis.QuantifierExpr;
import byteback.analysis.Vimp;
import soot.Local;
import soot.Value;
import soot.util.Chain;
import soot.util.Switch;

public class LogicForallExpr extends QuantifierExpr {

	public LogicForallExpr(final Chain<Local> freeLocals, final Value value) {
		super(freeLocals, value);
	}

	@Override
	protected String getSymbol() {
		return "∀";
	}

	@Override
	public void apply(final Switch sw) {
		((LogicExprVisitor<?>) sw).caseLogicForallExpr(this);
	}

	@Override
	public LogicForallExpr clone() {
		return new LogicForallExpr(cloneFreeLocals(), Vimp.cloneIfNecessary(getValue()));
	}

}
