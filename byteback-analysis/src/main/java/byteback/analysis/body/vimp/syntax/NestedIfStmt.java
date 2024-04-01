package byteback.analysis.body.vimp.syntax;

import soot.Unit;
import soot.Value;
import soot.jimple.Jimple;
import soot.jimple.internal.ImmediateBox;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.StmtBox;

/**
 * An if stmt that admits an immediate expression as its condition.
 *
 * @author paganma
 */
public class NestedIfStmt extends JIfStmt {

    public NestedIfStmt(final Value condition, final Unit target) {
        super(Jimple.v().newImmediateBox(condition),  Jimple.v().newStmtBox(target));
    }

}
