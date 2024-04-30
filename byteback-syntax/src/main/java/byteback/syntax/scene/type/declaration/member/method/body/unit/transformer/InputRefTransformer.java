package byteback.syntax.scene.type.declaration.member.method.body.unit.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.tag.ArgumentRefsTag;
import byteback.syntax.scene.type.declaration.member.method.body.tag.ArgumentRefsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.body.tag.InstanceRefTag;
import byteback.syntax.scene.type.declaration.member.method.body.tag.InstanceRefTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.body.unit.context.UnitContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.ArgumentRef;
import byteback.syntax.scene.type.declaration.member.method.body.value.ParameterRef;
import byteback.syntax.scene.type.declaration.member.method.body.value.InstanceRef;
import soot.*;
import soot.jimple.*;

public class InputRefTransformer extends UnitTransformer {

    private static final Lazy<InputRefTransformer> INSTANCE = Lazy.from(InputRefTransformer::new);

    public static InputRefTransformer v() {
        return INSTANCE.get();
    }

    private InputRefTransformer() {
    }

    @Override
    public void transformUnit(final UnitContext unitContext) {
        final UnitBox unitBox = unitContext.getUnitBox();
        final Unit unit = unitBox.getUnit();

        if (unit instanceof final IdentityStmt identityStmt) {
            final Body body = unitContext.getBodyContext().getBody();
            final Value leftOp = identityStmt.getLeftOp();
            final Value rightOp = identityStmt.getRightOp();
            final ParameterRef newRightOp;

            if (rightOp instanceof final soot.jimple.ParameterRef parameterRef) {
                final Type argumentType = parameterRef.getType();
                final int position = parameterRef.getIndex();
                final ArgumentRef argumentRef = Vimp.v().newArgumentRef(argumentType, position);
                ArgumentRefsTagAccessor.v().putIfAbsent(body, ArgumentRefsTag::new).addArgumentRef(argumentRef);
                newRightOp = argumentRef;
            } else if(rightOp instanceof final ThisRef thisRef) {
                final InstanceRef instanceRef = Vimp.v().newInstanceRef((RefType) thisRef.getType());
                InstanceRefTagAccessor.v().put(body, new InstanceRefTag(instanceRef));
                newRightOp = instanceRef;
            } else {
                return;
            }

            final AssignStmt assignStmt = Jimple.v().newAssignStmt(leftOp, newRightOp);
            unitBox.setUnit(assignStmt);
        }
    }

}
