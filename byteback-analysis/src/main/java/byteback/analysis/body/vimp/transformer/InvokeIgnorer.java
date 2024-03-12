package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.common.Hosts;
import byteback.analysis.common.namespace.BBLibNames;
import byteback.common.function.Lazy;
import soot.Body;
import byteback.analysis.model.MethodModel;
import soot.Unit;
import soot.jimple.InvokeStmt;

import java.util.Iterator;

public class InvokeIgnorer extends BodyTransformer {

    private static final Lazy<InvokeIgnorer> instance = Lazy.from(InvokeIgnorer::new);

    public static InvokeIgnorer v() {
        return instance.get();
    }

    @Override
    public void transformBody(final Body body) {
        final Iterator<Unit> unitsIterator = body.getUnits().snapshotIterator();

        while (unitsIterator.hasNext()) {
            final Unit unit = unitsIterator.next();

            if (unit instanceof InvokeStmt invokeStmt) {
                final MethodModel method = invokeStmt.getInvokeExpr().getMethod();

                if (Hosts.v().hasAnnotation(method, BBLibNames.IGNORE_ANNOTATION)) {
                    body.getUnits().remove(unit);
                }
            }
        }
    }

}
