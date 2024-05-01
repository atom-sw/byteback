package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import soot.*;
import soot.jimple.*;
import soot.util.Chain;

import java.util.Iterator;

public class ArgumentLocalTransformer extends BodyTransformer {


    private static final Lazy<ArgumentLocalTransformer> INSTANCE = Lazy.from(ArgumentLocalTransformer::new);

    private ArgumentLocalTransformer() {
    }

    public static ArgumentLocalTransformer v() {
        return INSTANCE.get();
    }

    @Override
    public void transformBody(final BodyContext bodyContext) {
        final Body body = bodyContext.getBody();
        final Chain<Local> locals = body.getLocals();
        final PatchingChain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = units.snapshotIterator();

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            if (unit instanceof IdentityStmt identityStmt
                    && identityStmt.getRightOp() instanceof final ParameterRef parameterRef) {
                final Type parameterType = parameterRef.getType();
                final int index = parameterRef.getIndex();
                final Local newLocal = Vimp.v().newArgumentLocal(parameterType, index);
                final Value assignee = identityStmt.getLeftOp();
                final AssignStmt assignStmt = Jimple.v().newAssignStmt(
                        assignee,
                        newLocal
                );
                units.swapWith(unit, assignStmt);
                locals.add(newLocal);
            }
        }
    }

}
