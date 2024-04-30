package byteback.syntax.scene.type.declaration.member.method.body.unit.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.unit.context.UnitContext;
import soot.*;
import soot.jimple.*;

/**
 * @author paganma
 */
public class ThrownAssignmentTransformer extends UnitTransformer {

    private static final Lazy<ThrownAssignmentTransformer> INSTANCE = Lazy.from(ThrownAssignmentTransformer::new);

    private ThrownAssignmentTransformer() {
    }

    public static ThrownAssignmentTransformer v() {
        return INSTANCE.get();
    }

    @Override
    public void transformUnit(final UnitContext unitContext) {
        final UnitBox unitBox = unitContext.getUnitBox();
        final Unit unit = unitBox.getUnit();

        if (unit instanceof final IdentityStmt identityStmt
                && identityStmt.getRightOp() instanceof CaughtExceptionRef) {
            final Unit thrownAssignStmt = Jimple.v().newAssignStmt(
                    identityStmt.getLeftOp(),
                    Vimp.v().newThrownLocal()
            );
            unitBox.setUnit(thrownAssignStmt);
        }
    }

}
