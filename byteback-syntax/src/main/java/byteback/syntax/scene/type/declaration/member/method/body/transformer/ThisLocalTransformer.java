package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.tag.ThisLocalTag;
import byteback.syntax.scene.type.declaration.member.method.body.tag.ThisLocalTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.body.value.ThisLocal;
import soot.*;
import soot.jimple.*;
import soot.util.Chain;

import java.util.Iterator;

public class ThisLocalTransformer extends BodyTransformer {

    private static final Lazy<ThisLocalTransformer> INSTANCE = Lazy.from(ThisLocalTransformer::new);

    private ThisLocalTransformer() {
    }

    public static ThisLocalTransformer v() {
        return INSTANCE.get();
    }

    @Override
    public void transformBody(final BodyContext bodyContext) {
        final SootMethod sootMethod = bodyContext.getSootMethod();

        if (sootMethod.isStatic()) {
            return;
        }

        final Body body = bodyContext.getBody();
        final Chain<Local> locals = body.getLocals();
        final PatchingChain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = units.snapshotIterator();
        final ThisLocal newLocal = Vimp.v().newThisLocal((RefType) body.getThisLocal().getType());
        ThisLocalTagAccessor.v().put(body, new ThisLocalTag(newLocal));
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
