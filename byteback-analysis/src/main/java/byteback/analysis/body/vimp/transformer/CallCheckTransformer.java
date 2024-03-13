package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.VimpValues;
import byteback.analysis.body.vimp.syntax.VoidConstant;
import byteback.common.function.Lazy;
import byteback.analysis.body.common.Body;
import byteback.analysis.body.jimple.syntax.Unit;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import soot.grimp.Grimp;
import byteback.analysis.body.jimple.syntax.CaughtExceptionRef;
import soot.util.Chain;

import java.util.Iterator;

public class CallCheckTransformer extends BodyTransformer {

    private static final Lazy<CallCheckTransformer> instance = Lazy.from(CallCheckTransformer::new);

    private CallCheckTransformer() {
    }

    public static CallCheckTransformer v() {
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

                if (VimpValues.v().isStatefulInvoke(value)) {
                    final Unit throwUnit = Grimp.v().newThrowStmt(Vimp.v().newCaughtExceptionRef());
                    units.insertAfter(throwUnit, unit);
                    final Unit elseBranch = units.getSuccOf(throwUnit);
                    final Unit ifUnit = Vimp.v().newIfStmt(makeCheckExpr(), elseBranch);
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
