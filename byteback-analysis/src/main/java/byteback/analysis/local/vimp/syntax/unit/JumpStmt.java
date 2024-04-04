package byteback.analysis.local.vimp.syntax.unit;

import byteback.analysis.local.vimp.syntax.Vimp;
import soot.Unit;
import soot.Value;
import soot.jimple.Jimple;
import soot.jimple.internal.JIfStmt;

/**
 * An if stmt that admits an immediate expression as its condition.
 *
 * @author paganma
 */
public class JumpStmt extends JIfStmt {

    public JumpStmt(final Value condition, final Unit target) {
        super(Vimp.v().newConditionExprBox(condition),  Jimple.v().newStmtBox(target));
    }

}
