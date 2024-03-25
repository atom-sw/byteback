package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.VimpStmtSwitch;
import soot.UnitPrinter;
import soot.Value;
import soot.util.Switch;

/**
 * A statement for declaring intermediate assertions.
 * @author paganma
 */
public class AssertStmt extends SpecificationStmt {

	public AssertStmt(final Value behaviorValue) {
		super(behaviorValue);
	}

	@Override
	public void apply(final Switch visitor) {
		if (visitor instanceof VimpStmtSwitch<?> vimpStmtSwitch) {
			vimpStmtSwitch.caseAssertionStmt(this);
		}
	}

	@Override
	public AssertStmt clone() {
		return new AssertStmt(Vimp.v().cloneIfNecessary(getCondition()));
	}

	@Override
	public void toString(final UnitPrinter printer) {
		printer.literal("assert ");
		getCondition().toString(printer);
	}

}
