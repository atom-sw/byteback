package byteback.syntax.scene.type.declaration.member.method.body.unit;

import soot.UnitPrinter;
import soot.Value;
import soot.jimple.Jimple;

/**
 * A statement for declaring loop invariants.
 *
 * @author paganma
 */
public class InvariantStmt extends SpecificationStmt implements DefaultCaseUnit {

    public InvariantStmt(final Value condition) {
        super(condition);
    }

    @Override
    public InvariantStmt clone() {
        return new InvariantStmt(Jimple.cloneIfNecessary(getCondition()));
    }

    @Override
    public void toString(final UnitPrinter printer) {
        printer.literal("invariant ");
        getCondition().toString(printer);
    }

}
