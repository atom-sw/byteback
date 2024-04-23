package byteback.syntax.scene.type.declaration.member.method.body.unit;

import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
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
        super(Jimple.v().newImmediateBox(condition), Jimple.v().newStmtBox(target));
    }

}
