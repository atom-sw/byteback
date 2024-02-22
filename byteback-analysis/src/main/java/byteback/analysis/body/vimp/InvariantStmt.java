package byteback.analysis.body.vimp;

import byteback.analysis.body.vimp.visitor.VimpStmtSwitch;
import soot.UnitPrinter;
import soot.Value;
import soot.util.Switch;

public class InvariantStmt extends LogicStmt {

	public InvariantStmt(final Value condition) {
		super(condition);
	}

	@Override
	public void apply(final Switch sw) {
		if (sw instanceof VimpStmtSwitch<?> vimpStmtSwitch) {
			vimpStmtSwitch.caseInvariantStmt(this);
		}
	}

	@Override
	public InvariantStmt clone() {
		return new InvariantStmt(Vimp.cloneIfNecessary(getCondition()));
	}

	@Override
	public void toString(final UnitPrinter up) {
		up.literal("invariant ");
		getCondition().toString(up);
	}

}
