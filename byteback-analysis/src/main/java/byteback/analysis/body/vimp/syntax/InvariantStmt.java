package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.VimpStmtSwitch;
import soot.UnitPrinter;
import soot.Value;
import soot.util.Switch;

/**
 * A statement for declaring loop invariants.
 * @author paganma
 */
public class InvariantStmt extends SpecificationStmt {

	public InvariantStmt(final Value behaviorValue) {
		super(behaviorValue);
	}

	@Override
	public void apply(final Switch visitor) {
		if (visitor instanceof VimpStmtSwitch<?> vimpStmtSwitch) {
			vimpStmtSwitch.caseInvariantStmt(this);
		}
	}

	@Override
	public InvariantStmt clone() {
		return new InvariantStmt(Vimp.cloneIfNecessary(getCondition()));
	}

	@Override
	public void toString(final UnitPrinter printer) {
		printer.literal("invariant ");
		getCondition().toString(printer);
	}

}
