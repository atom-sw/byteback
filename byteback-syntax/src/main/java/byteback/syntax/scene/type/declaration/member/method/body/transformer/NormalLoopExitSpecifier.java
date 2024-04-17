package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.syntax.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.context.BodyTransformationContext;
import byteback.syntax.scene.type.declaration.member.method.body.unit.AssertStmt;
import byteback.syntax.scene.type.declaration.member.method.body.value.VoidConstant;
import byteback.syntax.scene.type.declaration.member.method.body.unit.tag.ExceptionalTargetFlagger;
import byteback.common.function.Lazy;

import java.util.Set;

import soot.Body;
import soot.Unit;
import soot.Value;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.util.Chain;

/**
 * Specifies non-exceptional loop exits as such, by adding `assume @caughtexception == @void` at every non-exceptional
 * loop exit.
 *
 * @author paganma
 */
public class NormalLoopExitSpecifier extends BodyTransformer {

    private static final Lazy<NormalLoopExitSpecifier> INSTANCE = Lazy.from(NormalLoopExitSpecifier::new);

    private NormalLoopExitSpecifier() {
    }

    public static NormalLoopExitSpecifier v() {
        return INSTANCE.get();
    }

    @Override
    public void transformBody(final BodyTransformationContext bodyContext) {
        final Body body = bodyContext.getBody();
        final LoopFinder loopFinder = new LoopFinder();
        final Chain<Unit> units = body.getUnits();
        final Set<Loop> loops = loopFinder.getLoops(body);

        for (final Loop loop : loops) {
            for (final Unit loopExit : loop.getLoopExits()) {
                final CaughtExceptionRef exceptionRef = Vimp.v().newCaughtExceptionRef();
                final VoidConstant voidConstant = VoidConstant.v();
                final Value behaviorValue =
                        Jimple.v().newEqExpr(
                                exceptionRef,
                                voidConstant
                        );
                final AssertStmt assertUnit = Vimp.v().newAssertStmt(behaviorValue);
                final Unit target;

                // The `getTarget()` method for IfStmt is different to that of GotoStmt, hence the two matches.
                if (loopExit instanceof final IfStmt ifExit) {
                    target = ifExit.getTarget();
                } else if (loopExit instanceof final GotoStmt gotoStmt) {
                    target = gotoStmt.getTarget();
                } else {
                    continue;
                }

                // Insert the assumption only if exit target is not an exceptional target
                // (as tagged by GuardTransformer).
                if (ExceptionalTargetFlagger.v().get(target).isEmpty()) {
                    units.insertBefore(assertUnit, target);
                    target.redirectJumpsToThisTo(assertUnit);
                }
            }
        }
    }

}
