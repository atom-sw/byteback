package byteback.analysis.vimp;

import byteback.analysis.LogicStmtSwitch;
import byteback.analysis.Vimp;
import soot.UnitPrinter;
import soot.Value;
import soot.util.Switch;

public class AssertionStmt extends LogicStmt {

	public AssertionStmt(final Value condition) {
		super(condition);
	}

	public void toString(final UnitPrinter up) {
		up.literal("assert ");
		getCondition().toString(up);
	}

	@Override
	public void apply(final Switch sw) {
		if (sw instanceof LogicStmtSwitch) {
			((LogicStmtSwitch<?>) sw).caseAssertionStmt(this);
		}
	}

	@Override
	public AssertionStmt clone() {
		return new AssertionStmt(Vimp.cloneIfNecessary(getCondition()));
	}

}
