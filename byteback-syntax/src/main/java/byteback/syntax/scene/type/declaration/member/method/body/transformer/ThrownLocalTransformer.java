package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.tag.ThrownLocalTag;
import byteback.syntax.scene.type.declaration.member.method.body.tag.ThrownLocalTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.body.value.ThrownLocal;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import soot.*;
import soot.jimple.*;
import soot.util.Chain;

import java.util.Iterator;

public class ThrownLocalTransformer extends BodyTransformer {

    private static final Lazy<ThrownLocalTransformer> INSTANCE = Lazy.from(ThrownLocalTransformer::new);

    private ThrownLocalTransformer() {
    }

    public static ThrownLocalTransformer v() {
        return INSTANCE.get();
    }

    @Override
    public void transformBody(final BodyContext bodyContext) {
        final SootMethod sootMethod = bodyContext.getSootMethod();

        if (BehaviorTagMarker.v().hasTag(sootMethod)) {
            return;
        }

        final Body body = bodyContext.getBody();
        final Chain<Local> locals = body.getLocals();
        final PatchingChain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = units.snapshotIterator();
        final ThrownLocal newLocal = Vimp.v().newThrownLocal();
        ThrownLocalTagAccessor.v().put(body, new ThrownLocalTag(newLocal));
        locals.add(newLocal);

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            if (unit instanceof final IdentityStmt identityStmt
                    && identityStmt.getRightOp() instanceof CaughtExceptionRef) {
                final AssignStmt assignStmt = Jimple.v().newAssignStmt(
                        identityStmt.getLeftOp(),
                        newLocal
                );
                units.swapWith(unit, assignStmt);
            }
        }
    }

}
