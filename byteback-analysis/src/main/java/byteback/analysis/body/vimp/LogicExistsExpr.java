package byteback.analysis.body.vimp;

import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import soot.Local;
import soot.Value;
import soot.util.Chain;
import soot.util.Switch;

public class LogicExistsExpr extends QuantifierExpr {

	public LogicExistsExpr(final Chain<Local> freeLocals, final Value value) {
		super(freeLocals, value);
	}

	@Override
	protected String getSymbol() {
		return "∃";
	}

	@Override
	public void apply(final Switch sw) {
		if (sw instanceof VimpValueSwitch<?> vimpValueSwitch) {
			vimpValueSwitch.caseLogicExistsExpr(this);
		}
	}

	@Override
	public LogicExistsExpr clone() {
		return new LogicExistsExpr(cloneFreeLocals(), Vimp.cloneIfNecessary(getValue()));
	}

}
