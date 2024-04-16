package byteback.syntax.type.declaration.method.body.unit;

import soot.UnitPrinter;
import soot.Value;
import soot.jimple.Jimple;

/**
 * A statement for declaring intermediate assumptions.
 *
 * @author paganma
 */
public class AssumeStmt extends SpecificationStmt implements DefaultCaseUnit {

	public AssumeStmt(final Value condition) {
		super(condition);
	}

	@Override
	public AssumeStmt clone() {
		return new AssumeStmt(Jimple.cloneIfNecessary(getCondition()));
	}

	@Override
	public void toString(final UnitPrinter printer) {
		printer.literal("assume ");
		getCondition().toString(printer);
	}

}
