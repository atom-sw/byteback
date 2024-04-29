package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.tag.IgnoreTagMarker;
import byteback.syntax.tag.AnnotationTagReader;
import soot.Body;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeStmt;

import java.util.Iterator;

/**
 * Filters invocations to methods annotated with the @Ignore annotation. More precisely, if a unit contains an
 * invocation to an ignored method, it is removed from the body.
 *
 * @author paganma
 * @see BBLibNames
 */
public class InvokeFilter extends BodyTransformer {

    private static final Lazy<InvokeFilter> INSTANCE = Lazy.from(InvokeFilter::new);

    public static InvokeFilter v() {
        return INSTANCE.get();
    }

    @Override
    public void transformBody(final BodyContext bodyContext) {
        final Body body = bodyContext.getBody();
        final PatchingChain<Unit> units = body.getUnits();
        final Iterator<Unit> unitsIterator = units.snapshotIterator();

        while (unitsIterator.hasNext()) {
            final Unit unit = unitsIterator.next();

            if (unit instanceof final InvokeStmt invokeStmt) {
                final SootMethod invokedMethod = invokeStmt.getInvokeExpr().getMethod();

                if (IgnoreTagMarker.v().hasTag(invokedMethod)) {
                    units.remove(unit);
                }
            }
        }
    }

}
