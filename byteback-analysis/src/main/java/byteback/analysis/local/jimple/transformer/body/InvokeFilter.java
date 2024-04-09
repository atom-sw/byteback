package byteback.analysis.local.jimple.transformer.body;

import byteback.analysis.common.tag.AnnotationReader;
import byteback.analysis.local.common.transformer.body.BodyTransformer;
import byteback.analysis.common.name.BBLibNames;
import byteback.common.function.Lazy;

import java.util.Iterator;

import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeStmt;

/**
 * Filters invocations to methods annotated with the @Ignore annotation. More precisely, if a unit contains an
 * invocation to an ignored method, it is removed from the body.
 * @see BBLibNames
 *
 * @author paganma
 */
public class InvokeFilter extends BodyTransformer {

    private static final Lazy<InvokeFilter> instance = Lazy.from(InvokeFilter::new);

    public static InvokeFilter v() {
        return instance.get();
    }

    @Override
    public void transformBody(final Body body) {
        final Iterator<Unit> unitsIterator = body.getUnits().snapshotIterator();

        while (unitsIterator.hasNext()) {
            final Unit unit = unitsIterator.next();

            if (unit instanceof InvokeStmt invokeStmt) {
                final SootMethod method = invokeStmt.getInvokeExpr().getMethod();

                if (AnnotationReader.v().hasAnnotation(method, BBLibNames.IGNORE_ANNOTATION)) {
                    body.getUnits().remove(unit);
                }
            }
        }
    }

}
