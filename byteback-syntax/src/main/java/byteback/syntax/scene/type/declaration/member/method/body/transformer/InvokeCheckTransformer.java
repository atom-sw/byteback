package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.transformer.context.BodyTransformationContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.VoidConstant;
import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpEffectEvaluator;
import byteback.syntax.Vimp;
import byteback.common.function.Lazy;

import java.util.Iterator;

import soot.*;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.Jimple;

/**
 * Introduces explicit guards checking if the method that was just invoked threw an exception. The effect introduced by
 * this transformation is: if after invoking a method @caughtexception is not @void, @caughtexception must be thrown
 * again, otherwise execution can resume as normal.
 *
 * @author paganma
 */
public class InvokeCheckTransformer extends BodyTransformer {

    private static final Lazy<InvokeCheckTransformer> INSTANCE = Lazy.from(InvokeCheckTransformer::new);

    private InvokeCheckTransformer() {
    }

    public static InvokeCheckTransformer v() {
        return INSTANCE.get();
    }

    @Override
    public void transformBody(final BodyTransformationContext bodyContext) {
        final Body body = bodyContext.getBody();
        final PatchingChain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = units.snapshotIterator();

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            for (final ValueBox valueBox : unit.getUseBoxes()) {
                final Value value = valueBox.getValue();

                if (VimpEffectEvaluator.v().isStatefulInvoke(value)) {
                    final Unit thenBranch = Jimple.v().newThrowStmt(Vimp.v().newCaughtExceptionRef());
                    units.insertAfter(thenBranch, unit);
                    final Unit elseBranch = units.getSuccOf(thenBranch);
                    final Unit ifUnit = Jimple.v().newIfStmt(makeCheckExpr(), elseBranch);
                    units.insertAfter(ifUnit, unit);
                }
            }
        }
    }

    private Value makeCheckExpr() {
        final CaughtExceptionRef caughtExceptionRef = Vimp.v().newCaughtExceptionRef();

        return Jimple.v().newEqExpr(caughtExceptionRef, VoidConstant.v());
    }

}
