package byteback.analysis.body.vimp.transformer;

import byteback.analysis.common.namespace.BBLibNames;
import byteback.analysis.scene.Hosts;
import byteback.common.function.Lazy;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeStmt;

public class InvokeIgnorer extends BodyTransformer {

    private static final Lazy<InvokeIgnorer> instance = Lazy.from(InvokeIgnorer::new);

    public static InvokeIgnorer v() {
        return instance.get();
    }

    @Override
    public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        final Iterator<Unit> unitsIterator = body.getUnits().snapshotIterator();

        while (unitsIterator.hasNext()) {
            final Unit unit = unitsIterator.next();

            if (unit instanceof InvokeStmt invokeStmt) {
                final SootMethod method = invokeStmt.getInvokeExpr().getMethod();

                if (Hosts.hasAnnotation(method, BBLibNames.IGNORE_ANNOTATION)) {
                    body.getUnits().remove(unit);
                }
            }
        }
    }

}
