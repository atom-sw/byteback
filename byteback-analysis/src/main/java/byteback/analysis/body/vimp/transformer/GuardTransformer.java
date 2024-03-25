package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.syntax.TrapNestTree;
import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.vimp.Vimp;
import byteback.common.function.Lazy;

import java.util.*;

import soot.Body;
import soot.RefType;
import soot.Trap;
import soot.Unit;
import soot.Value;
import soot.grimp.Grimp;
import soot.jimple.*;
import soot.util.Chain;

/**
 * Transforms throw instructions into explicit branching instructions.
 * Given a statement `throw e`, the transformation introduces an assignment `@caughtexception := e`.
 * For each active trap at that point [e, handler], we then introduce an instance check:
 * ``` java
 * if (@caughtexception instanceof e) goto handler;
 * ```
 * At the end of these checks, we simply attach a `return` statement, signaling that the method will return without
 * yielding any value, leaving the caller method to check for eventually thrown exceptions.
 * @author paganma
 */
public class GuardTransformer extends BodyTransformer {

    private static final Lazy<GuardTransformer> instance = Lazy.from(GuardTransformer::new);

    private GuardTransformer() {
    }

    public static GuardTransformer v() {
        return instance.get();
    }

    @Override
    public void transformBody(final Body body) {
        final Chain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = units.snapshotIterator();

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            if (unit instanceof ThrowStmt throwUnit) {
                final Unit baseUnit = Grimp.v().newReturnVoidStmt();

                units.insertBefore(baseUnit, throwUnit);
                throwUnit.redirectJumpsToThisTo(baseUnit);
                units.remove(throwUnit);

                // If we are throwing the current @caughtexception, then there is no need to assign it.
                if (!(throwUnit.getOp() instanceof CaughtExceptionRef)) {
                    final Unit assignUnit = Grimp.v().newAssignStmt(Vimp.v().newCaughtExceptionRef(), throwUnit.getOp());
                    units.insertBefore(assignUnit, baseUnit);
                    baseUnit.redirectJumpsToThisTo(assignUnit);
                }
            }
        }
    }

}
