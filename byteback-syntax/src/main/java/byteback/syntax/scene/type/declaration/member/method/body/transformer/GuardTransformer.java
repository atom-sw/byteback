package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.ExprAggregator;
import byteback.syntax.scene.type.declaration.member.method.body.unit.iterator.TrapCollectingIterator;
import byteback.syntax.scene.type.declaration.member.method.body.unit.tag.ExceptionalTargetFlagger;
import byteback.syntax.Vimp;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.Jimple;
import soot.jimple.ThrowStmt;

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

    private static final Lazy<GuardTransformer> INSTANCE = Lazy.from(GuardTransformer::new);

    private GuardTransformer() {
    }

    public static GuardTransformer v() {
        return INSTANCE.get();
    }

    @Override
    public void transformBody(final BodyContext bodyContext) {
        final Body body = bodyContext.getBody();
        final PatchingChain<Unit> units = body.getUnits();
        final var unitIterator = new TrapCollectingIterator(units, body.getTraps());
        final var exprConstructor = new ExprAggregator(body);

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            if (unit instanceof ThrowStmt throwUnit) {
                final Unit baseUnit = Vimp.v().newYieldStmt();
                units.insertBefore(baseUnit, throwUnit);

                // If we are throwing the current @caughtexception, then there is no need to assign it.
                if (!(throwUnit.getOp() instanceof CaughtExceptionRef)) {
                    final CaughtExceptionRef caughtExceptionRef = Vimp.v().newCaughtExceptionRef();
                    final Unit assignUnit = Jimple.v().newAssignStmt(caughtExceptionRef, throwUnit.getOp());
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
                    final Immediate checkValue = exprConstructor.apply(
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