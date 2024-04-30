package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.ReturnLocal;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import soot.*;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.util.Chain;

import java.util.Iterator;

/**
 * Removes return statements and replaces them to an assignment to the @return reference followed by a `yield`
 * statement.
 *
 * @author paganma
 */
public class ReturnLocalTransformer extends BodyTransformer {

    private static final Lazy<ReturnLocalTransformer> INSTANCE = Lazy.from(ReturnLocalTransformer::new);

    private ReturnLocalTransformer() {
    }

    public static ReturnLocalTransformer v() {
        return INSTANCE.get();
    }

    @Override
    public void transformBody(final BodyContext bodyContext) {
        final SootMethod sootMethod = bodyContext.getSootMethod();

        if (BehaviorTagMarker.v().hasTag(sootMethod) || sootMethod.getReturnType() == VoidType.v()) {
            return;
        }

        final Body body = bodyContext.getBody();
        final Chain<Local> locals = body.getLocals();
        final Type returnType = body.getMethod().getReturnType();
        final Chain<Unit> units = body.getUnits();
        final ReturnLocal returnLocal = Vimp.v().newReturnLocal(returnType);
        final Iterator<Unit> unitIterator = body.getUnits().snapshotIterator();
        locals.add(returnLocal);

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            if (unit instanceof final ReturnStmt returnStmt) {
                final Value returnValue = returnStmt.getOp();
                final Unit returnAssignUnit = Jimple.v().newAssignStmt(returnLocal, returnValue);
                units.insertBefore(returnAssignUnit, returnStmt);
                returnStmt.redirectJumpsToThisTo(returnAssignUnit);
                returnAssignUnit.addAllTagsOf(returnStmt);
                units.swapWith(returnStmt, Vimp.v().newYieldStmt());
            } else if (unit instanceof final ReturnVoidStmt returnVoidStmt) {
                units.swapWith(returnVoidStmt, Vimp.v().newYieldStmt());
            }
        }
    }

}
