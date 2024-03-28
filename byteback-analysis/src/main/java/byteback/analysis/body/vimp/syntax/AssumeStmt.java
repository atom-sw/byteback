package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import soot.UnitPrinter;
import soot.Value;

/**
 * A statement for declaring intermediate assumptions.
 *
 * @author paganma
 */
public class AssumeStmt extends SpecificationStmt {

	public AssumeStmt(final Value condition) {
		super(condition);
	}

	@Override
	public AssumeStmt clone() {
		return new AssumeStmt(Vimp.v().cloneIfNecessary(getCondition()));
	}

	@Override
	public void toString(final UnitPrinter printer) {
		printer.literal("assume ");
		getCondition().toString(printer);
	}

}
