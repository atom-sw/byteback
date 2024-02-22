package byteback.analysis.body.vimp;

import byteback.analysis.body.vimp.visitor.VimpStmtSwitch;
import soot.UnitPrinter;
import soot.Value;
import soot.util.Switch;

public class AssertionStmt extends LogicStmt {

	public AssertionStmt(final Value condition) {
		super(condition);
	}

	@Override
	public void apply(final Switch sw) {
		if (sw instanceof VimpStmtSwitch<?> vimpStmtSwitch) {
			vimpStmtSwitch.caseAssertionStmt(this);
		}
	}

	@Override
	public AssertionStmt clone() {
		return new AssertionStmt(Vimp.cloneIfNecessary(getCondition()));
	}

	@Override
	public void toString(final UnitPrinter up) {
		up.literal("assert ");
		getCondition().toString(up);
	}

}
