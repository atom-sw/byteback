package byteback.analysis.body.vimp.transformer;

import byteback.analysis.common.namespace.BBLibNamespace;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.VoidConstant;
import byteback.common.Lazy;

import java.util.Iterator;
import java.util.Map;

import soot.*;
import soot.grimp.Grimp;
import soot.grimp.GrimpBody;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.util.Chain;

public class CallCheckTransformer extends BodyTransformer {

    private static final Lazy<CallCheckTransformer> instance = Lazy.from(CallCheckTransformer::new);

    private CallCheckTransformer() {
    }

    public static CallCheckTransformer v() {
        return instance.get();
    }

    @Override
    public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        transformBody(body);
    }

    public Value makeCheckExpr() {
        final CaughtExceptionRef caughtExceptionRef = Vimp.v().newCaughtExceptionRef();
        return Grimp.v().newEqExpr(caughtExceptionRef, VoidConstant.v());
    }

    public void transformBody(final Body body) {
        final Iterator<Unit> unitIterator = body.getUnits().snapshotIterator();
        final Chain<Unit> units = body.getUnits();

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            for (final ValueBox vbox : unit.getUseAndDefBoxes()) {
                final Value value = vbox.getValue();

                if (value instanceof InvokeExpr invokeExpr) {
                    final SootMethod invokedMethod = invokeExpr.getMethod();

                    if (!BBLibNamespace.isPureMethod(invokedMethod)
                            && !BBLibNamespace.isAnnotationClass(invokedMethod.getDeclaringClass())) {
                        final Unit throwUnit = Grimp.v().newThrowStmt(Vimp.v().newCaughtExceptionRef());
                        units.insertAfter(throwUnit, unit);
                        final Unit elseBranch = units.getSuccOf(throwUnit);
                        final Unit ifUnit = Vimp.v().newIfStmt(makeCheckExpr(), elseBranch);
                        units.insertAfter(ifUnit, unit);
                    }
                }
            }
        }
    }

}
