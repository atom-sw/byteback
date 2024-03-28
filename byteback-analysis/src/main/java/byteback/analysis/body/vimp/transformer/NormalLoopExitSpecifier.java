package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.vimp.NestedExprConstructor;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.syntax.AssertStmt;
import byteback.analysis.body.vimp.syntax.AssumeStmt;
import byteback.analysis.body.vimp.syntax.VoidConstant;
import byteback.analysis.body.vimp.tag.ExceptionalBranchTag;
import byteback.analysis.common.Hosts;
import byteback.common.function.Lazy;

import java.util.Set;

import soot.Body;
import soot.Scene;
import soot.Unit;
import soot.Value;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
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

    private static final Lazy<NormalLoopExitSpecifier> instance = Lazy.from(NormalLoopExitSpecifier::new);

    private NormalLoopExitSpecifier() {
    }

    public static NormalLoopExitSpecifier v() {
        return instance.get();
    }

    @Override
    public void transformBody(final Body body) {
        final LoopFinder loopFinder = new LoopFinder();
        final Chain<Unit> units = body.getUnits();
        final Set<Loop> loops = loopFinder.getLoops(body);
        final var exprConstructor = new NestedExprConstructor(body);

        for (final Loop loop : loops) {
            for (final Unit loopExit : loop.getLoopExits()) {
                final CaughtExceptionRef exceptionRef = Vimp.v().newCaughtExceptionRef();
                final VoidConstant voidConstant = VoidConstant.v();
                final Value behaviorValue =
                        exprConstructor.make(
                                Vimp.v()::newEqExpr,
                                exceptionRef,
                                voidConstant
                        );
                final AssertStmt assertUnit = Vimp.v().newAssertStmt(behaviorValue);
                final Unit target;

                // The `getTarget()` method for IfStmt is different to that of GotoStmt, hence the two matches.
                if (loopExit instanceof IfStmt ifExit) {
                    target = ifExit.getTarget();
                } else if (loopExit instanceof GotoStmt gotoStmt) {
                    target = gotoStmt.getTarget();
                } else {
                    continue;
                }

                if (!Hosts.v().hasTag(target, ExceptionalBranchTag.NAME)) {
                    units.insertBefore(assertUnit, target);
                    target.redirectJumpsToThisTo(assertUnit);
                }
            }
        }
    }

}
