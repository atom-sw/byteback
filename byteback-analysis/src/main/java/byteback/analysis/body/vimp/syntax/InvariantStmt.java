package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.VimpStmtSwitch;
import soot.UnitPrinter;
import byteback.analysis.body.common.syntax.Value;
import soot.util.Switch;

public class InvariantStmt extends SpecificationStmt {

    public InvariantStmt(final Value condition) {
        super(condition);
    }
}
