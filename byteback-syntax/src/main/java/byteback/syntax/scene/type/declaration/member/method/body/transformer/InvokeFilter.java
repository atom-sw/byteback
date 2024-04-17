package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.transformer.context.BodyTransformationContext;
import byteback.syntax.tag.AnnotationReader;
import byteback.syntax.name.BBLibNames;
import byteback.common.function.Lazy;

import java.util.Iterator;

import soot.Body;
import soot.PatchingChain;
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

    private static final Lazy<InvokeFilter> INSTANCE = Lazy.from(InvokeFilter::new);

    public static InvokeFilter v() {
        return INSTANCE.get();
    }

    @Override
    public void transformBody(final BodyTransformationContext bodyContext) {
        final Body body = bodyContext.getBody();
        final PatchingChain<Unit> units = body.getUnits();
        final Iterator<Unit> unitsIterator = units.snapshotIterator();

        while (unitsIterator.hasNext()) {
            final Unit unit = unitsIterator.next();

            if (unit instanceof final InvokeStmt invokeStmt) {
                final SootMethod method = invokeStmt.getInvokeExpr().getMethod();

                if (AnnotationReader.v().hasAnnotation(method, BBLibNames.IGNORE_ANNOTATION)) {
                    units.remove(unit);
                }
            }
        }
    }

}
