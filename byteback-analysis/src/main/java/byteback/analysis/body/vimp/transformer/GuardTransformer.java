package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.syntax.TrapRangeMap;
import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.syntax.VoidConstant;
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
        final var trapRanges = new TrapRangeMap(body);
        units.addFirst(Grimp.v().newAssignStmt(Vimp.v().newCaughtExceptionRef(), VoidConstant.v()));

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            if (unit instanceof ThrowStmt throwUnit) {
                final Unit retUnit = Grimp.v().newReturnVoidStmt();

                units.insertBefore(retUnit, throwUnit);
                throwUnit.redirectJumpsToThisTo(retUnit);
                units.remove(throwUnit);

                final Unit assignUnit;

                if (throwUnit.getOp() instanceof CaughtExceptionRef) {
                    assignUnit = units.getPredOf(retUnit);
                } else {
                    assignUnit = Grimp.v().newAssignStmt(Vimp.v().newCaughtExceptionRef(), throwUnit.getOp());
                    units.insertBefore(assignUnit, retUnit);
                    retUnit.redirectJumpsToThisTo(assignUnit);
                }

                Unit indexUnit = assignUnit;

                if (throwUnit.getOp().getType() instanceof RefType) {
                    final Trap[] activeTraps = trapRanges.trapsAt(throwUnit).toArray(Trap[]::new);

                    for (int i = activeTraps.length - 1; i >= 0; --i) {
                        final Trap activeTrap = activeTraps[i];
                        final RefType trapType = activeTrap.getException().getType();
                        final Value behavior = Jimple.v().newInstanceOfExpr(Vimp.v().newCaughtExceptionRef(), trapType);
                        final Unit ifUnit = Jimple.v().newIfStmt(behavior, activeTrap.getHandlerUnit());
                        units.insertAfter(ifUnit, indexUnit);
                        indexUnit = ifUnit;
                    }
                }
            }
        }
    }

}
