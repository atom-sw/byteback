package byteback.analysis.local.vimp.syntax.unit;

import byteback.analysis.local.common.syntax.unit.DefaultCaseUnit;
import byteback.analysis.local.vimp.syntax.unit.SpecificationStmt;
import soot.UnitPrinter;
import soot.Value;
import soot.jimple.Jimple;

/**
 * A statement for declaring intermediate assertions.
 *
 * @author paganma
 */
public class AssertStmt extends SpecificationStmt implements DefaultCaseUnit {

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
