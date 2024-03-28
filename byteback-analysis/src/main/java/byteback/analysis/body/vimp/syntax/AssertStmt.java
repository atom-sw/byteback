package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import soot.UnitPrinter;
import soot.Value;

/**
 * A statement for declaring intermediate assertions.
 *
 * @author paganma
 */
public class AssertStmt extends SpecificationStmt {

	public AssertStmt(final Value behaviorValue) {
		super(behaviorValue);
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
