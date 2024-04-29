package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import soot.*;
import soot.jimple.IdentityStmt;
import soot.jimple.Jimple;
import soot.jimple.ParameterRef;
import soot.jimple.ThisRef;
import soot.jimple.internal.JimpleLocal;
import soot.util.Chain;

import java.util.Iterator;

public class ParameterLocalFinalizer extends BodyTransformer {

    private static final Lazy<ParameterLocalFinalizer> INSTANCE = Lazy.from(ParameterLocalFinalizer::new);

    public static ParameterLocalFinalizer v() {
        return INSTANCE.get();
    }

    private ParameterLocalFinalizer() {
    }

    @Override
    public void transformBody(final BodyContext bodyContext) {
        final SootMethod sootMethod = bodyContext.getSootMethod();

        if (BehaviorTagMarker.v().hasTag(sootMethod)) {
            return;
        }

        final Body body = bodyContext.getBody();
        final Chain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = units.snapshotIterator();

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            if (unit instanceof final IdentityStmt identityStmt) {
                final Value rightOp = identityStmt.getRightOp();

                if (rightOp instanceof ParameterRef || rightOp instanceof ThisRef) {
                    final Local parameterLocal = (Local) identityStmt.getLeftOp();
                    final String parameterLocalName = parameterLocal.getName();
                    final Type parameterLocalType = parameterLocal.getType();
                    final var newParameterLocal = new JimpleLocal(parameterLocalName + "#", parameterLocalType);
                    identityStmt.setLeftOp(newParameterLocal);
                    bodyContext.getBody().getLocals().add(newParameterLocal);
                    units.insertAfter(Jimple.v().newAssignStmt(parameterLocal, newParameterLocal), identityStmt);
                }
            }
        }
    }

}
