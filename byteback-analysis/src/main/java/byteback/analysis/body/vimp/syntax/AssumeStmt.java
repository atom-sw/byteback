package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.VimpStmtSwitch;
import soot.UnitPrinter;
import byteback.analysis.body.common.syntax.Value;
import soot.util.Switch;

public class AssumeStmt extends SpecificationStmt {

    public AssumeStmt(final Value condition) {
        super(condition);
    }

    @Override
    public void apply(final Switch visitor) {
        if (visitor instanceof VimpStmtSwitch<?> vimpStmtSwitch) {
            vimpStmtSwitch.caseAssumptionStmt(this);
        }
    }

    @Override
    public AssumeStmt clone() {
        return new AssumeStmt(Vimp.cloneIfNecessary(getCondition()));
    }

    @Override
    public void toString(final UnitPrinter printer) {
        printer.literal("assume ");
        getCondition().toString(printer);
    }

}
