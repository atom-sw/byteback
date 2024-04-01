package byteback.analysis.body.vimp.syntax;

import soot.UnitPrinter;
import soot.Value;
import soot.jimple.Jimple;

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
		return new AssertStmt(Jimple.cloneIfNecessary(getCondition()));
	}

	@Override
	public void toString(final UnitPrinter printer) {
		printer.literal("assert ");
		getCondition().toString(printer);
	}

}
