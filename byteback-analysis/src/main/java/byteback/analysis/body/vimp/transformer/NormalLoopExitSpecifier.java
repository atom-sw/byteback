package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.syntax.AssumeStmt;
import byteback.analysis.body.vimp.syntax.ConcreteCaughtExceptionRef;
import byteback.analysis.body.vimp.syntax.VoidConstant;
import byteback.common.function.Lazy;

import java.util.Iterator;
import java.util.Set;

import soot.Body;
import soot.Unit;
import soot.Value;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.toolkits.graph.LoopNestTree;
import soot.util.Chain;

/**
 * Specifies non-exceptional loop exits as such, by adding `assume @caughtexception == @void` at every non-exceptional
 * loop exit.
 * This transformation must be applied before any other transformation expanding the exceptional controlflow of the
 * program.
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

        for (final Loop loop : loops) {
            for (final Unit loopExit : loop.getLoopExits()) {
                final CaughtExceptionRef exceptionRef = Vimp.v().newCaughtExceptionRef();
                final VoidConstant voidConstant = VoidConstant.v();
                final Value behaviorValue = Vimp.v().newEqExpr(exceptionRef, voidConstant);
                final AssumeStmt assumeStmt = Vimp.v().newAssumeStmt(behaviorValue);
                units.insertBefore(assumeStmt, loopExit);
                loopExit.redirectJumpsToThisTo(assumeStmt);
            }
        }
    }

}
