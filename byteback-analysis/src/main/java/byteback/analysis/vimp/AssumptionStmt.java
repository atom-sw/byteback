package byteback.analysis.vimp;

import byteback.analysis.LogicStmtSwitch;
import byteback.analysis.Vimp;
import soot.UnitPrinter;
import soot.Value;
import soot.util.Switch;

public class AssumptionStmt extends LogicStmt {

	public AssumptionStmt(final Value condition) {
		super(condition);
	}

	public void toString(final UnitPrinter up) {
		up.literal("assume ");
		getCondition().toString(up);
	}

	@Override
	public void apply(final Switch sw) {
		if (sw instanceof LogicStmtSwitch<?> logicSwitch) {
			logicSwitch.caseAssumptionStmt(this);
		}
	}

	@Override
	public AssumptionStmt clone() {
		return new AssumptionStmt(Vimp.cloneIfNecessary(getCondition()));
	}

}
