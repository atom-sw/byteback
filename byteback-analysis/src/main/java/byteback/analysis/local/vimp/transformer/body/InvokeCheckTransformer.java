package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.local.common.transformer.body.BodyTransformer;
import byteback.analysis.local.vimp.analyzer.value.VimpEffectEvaluator;
import byteback.analysis.local.vimp.syntax.Vimp;
import byteback.analysis.local.vimp.syntax.value.VoidConstant;
import byteback.common.function.Lazy;

import java.util.Iterator;

import soot.*;
import soot.grimp.Grimp;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.Jimple;
import soot.util.Chain;

/**
 * Introduces explicit guards checking if the method that was just invoked threw an exception. The effect introduced by
 * this transformation is: if after invoking a method @caughtexception is not @void, @caughtexception must be thrown
 * again, otherwise execution can resume as normal.
 *
 * @author paganma
 */
public class InvokeCheckTransformer extends BodyTransformer {

    private static final Lazy<InvokeCheckTransformer> instance = Lazy.from(InvokeCheckTransformer::new);

    private InvokeCheckTransformer() {
    }

    public static InvokeCheckTransformer v() {
        return instance.get();
    }

    @Override
    public void transformBody(final Body body) {
        final Iterator<Unit> unitIterator = body.getUnits().snapshotIterator();
        final Chain<Unit> units = body.getUnits();

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            for (final ValueBox valueBox : unit.getUseBoxes()) {
                final Value value = valueBox.getValue();

                if (VimpEffectEvaluator.v().isStatefulInvoke(value)) {
                    final Unit thenBranch = Grimp.v().newThrowStmt(Vimp.v().newCaughtExceptionRef());
                    units.insertAfter(thenBranch, unit);
                    final Unit elseBranch = units.getSuccOf(thenBranch);
                    final Unit ifUnit = Jimple.v().newIfStmt(makeCheckExpr(), elseBranch);
                    units.insertAfter(ifUnit, unit);
                }
            }
        }
    }

    public Value makeCheckExpr() {
        final CaughtExceptionRef caughtExceptionRef = Vimp.v().newCaughtExceptionRef();
        return Grimp.v().newEqExpr(caughtExceptionRef, VoidConstant.v());
    }

}
