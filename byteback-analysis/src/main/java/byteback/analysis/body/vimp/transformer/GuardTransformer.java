package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.syntax.TrapCollector;
import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.vimp.NestedExprConstructor;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.tag.ExceptionalTargetFlagger;
import byteback.common.function.Lazy;
import soot.Body;
import soot.Immediate;
import soot.Trap;
import soot.Unit;
import soot.grimp.Grimp;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.Jimple;
import soot.jimple.ThrowStmt;
import soot.util.Chain;

/**
 * Transforms throw instructions into explicit branching instructions.
 * Given a statement `throw e`, the transformation introduces an assignment `@caughtexception := e`.
 * For each active trap at that point [e, handler], we then introduce an instance check:
 * ``` java
 * if (@caughtexception instanceof e) goto handler;
 * ```
 * At the end of these checks, we simply append a `return` statement, signaling that the method will return without
 * yielding any value, leaving the caller method to check for the exceptions that were unchecked in the called method.
 *
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
        final var unitIterator = new TrapCollector(units.snapshotIterator(), body.getTraps());
        final var exprConstructor = new NestedExprConstructor(body);

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            if (unit instanceof ThrowStmt throwUnit) {
                final Unit baseUnit = Grimp.v().newReturnVoidStmt();
                units.insertBefore(baseUnit, throwUnit);

                // If we are throwing the current @caughtexception, then there is no need to assign it.
                if (!(throwUnit.getOp() instanceof CaughtExceptionRef)) {
                    final Unit assignUnit = Grimp.v().newAssignStmt(Vimp.v().newCaughtExceptionRef(), throwUnit.getOp());
                    units.insertBefore(assignUnit, baseUnit);
                    baseUnit.redirectJumpsToThisTo(assignUnit);
                }


                // Traps are collected in the same order in which they are opened. This means that the first element
                // of the queue is the innermost trap at the current statement. This means that the active traps are
                // sorted from innermost to outermost.
                // Here iterate the active traps from the innermost, and add branching conditions before the return
                // statement. This means that the conditions will be checked in the same order in which the traps have
                // been opened, hence making the jumps semantically equivalent to the control flow specified by the
                // exception table.
                for (final Trap trap : unitIterator.getActiveTraps()) {
                    final Immediate checkValue = exprConstructor.make(
                            Jimple.v().newInstanceOfExpr(
                                    Vimp.v().newCaughtExceptionRef(),
                                    trap.getException().getType()
                            )
                    );
                    final Unit branchUnit = Vimp.v().newIfStmt(checkValue, trap.getHandlerUnit());
                    ExceptionalTargetFlagger.v().flag(branchUnit);
                    units.insertBefore(branchUnit, baseUnit);
                }

                throwUnit.redirectJumpsToThisTo(baseUnit);
                baseUnit.addAllTagsOf(throwUnit);
                units.remove(throwUnit);
            }
        }
    }

}
