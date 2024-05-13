package byteback.syntax.scene.type.declaration.member.method.body.unit;

import soot.UnitPrinter;
import soot.Value;
import soot.jimple.Jimple;

/**
 * A statement for declaring intermediate assertions.
 *
 * @author paganma
 */
public class AssertStmt extends SpecificationStmt implements DefaultCaseUnit {

	public AssertStmt(final Value condition) {
		super(condition);
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
