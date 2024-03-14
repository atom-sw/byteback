package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.common.syntax.stmt.Unit;
import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.jimple.syntax.expr.CaughtExceptionRef;
import byteback.analysis.body.jimple.syntax.expr.EqExpr;
import byteback.analysis.body.jimple.syntax.stmt.IfStmt;
import byteback.analysis.body.jimple.syntax.stmt.ThrowStmt;
import byteback.analysis.body.vimp.VimpValues;
import byteback.analysis.body.vimp.syntax.VoidConstant;
import byteback.analysis.common.syntax.Chain;
import byteback.common.function.Lazy;
import byteback.analysis.body.common.syntax.Body;
import byteback.analysis.body.common.syntax.expr.Value;

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
                    final Unit throwUnit = new ThrowStmt(CaughtExceptionRef.v());
                    units.insertAfter(throwUnit, unit);
                    final Unit elseBranch = units.getSuccOf(throwUnit);
                    final Unit ifUnit = new IfStmt(makeCheckExpr(), elseBranch);
                    units.insertAfter(ifUnit, unit);
                }
            }
        }
    }

    public Value makeCheckExpr() {
        final CaughtExceptionRef caughtExceptionRef = CaughtExceptionRef.v();
        return new EqExpr(caughtExceptionRef, VoidConstant.v());
    }

}
