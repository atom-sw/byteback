package byteback.analysis.vimp;

import byteback.analysis.LogicStmtSwitch;
import byteback.analysis.Vimp;
import soot.UnitPrinter;
import soot.Value;
import soot.util.Switch;

public class InvariantStmt extends LogicStmt {

	public InvariantStmt(final Value condition) {
		super(condition);
	}

	@Override
	public void toString(final UnitPrinter up) {
		up.literal("invariant ");
		getCondition().toString(up);
	}

	@Override
	public void apply(final Switch sw) {
		if (sw instanceof LogicStmtSwitch) {
			((LogicStmtSwitch<?>) sw).caseInvariantStmt(this);
		}
	}

	@Override
	public InvariantStmt clone() {
		return new InvariantStmt(Vimp.cloneIfNecessary(getCondition()));
	}

}
