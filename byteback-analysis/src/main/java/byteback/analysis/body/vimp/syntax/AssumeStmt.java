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
}
