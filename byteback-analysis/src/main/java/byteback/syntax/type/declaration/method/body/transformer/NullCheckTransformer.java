package byteback.syntax.type.declaration.method.body.transformer;

import byteback.syntax.Vimp;

import java.util.Optional;

import soot.*;
import soot.jimple.*;

/**
 * Introduces explicit index checks before every array dereference.
 *
 * @author paganma
 */
public class NullCheckTransformer extends CheckTransformer {

    public NullCheckTransformer(final Scene scene) {
        super(scene, "java.lang.NullPointerException");
    }

    @Override
    public Optional<Value> makeUnitCheck(final Unit unit) {
        Value base = null;

        if (unit instanceof final AssignStmt assignStmt) {
            if (assignStmt.getLeftOp() instanceof final Ref ref) {
                if (ref instanceof final InstanceFieldRef instanceFieldRef) {
                    base = instanceFieldRef.getBase();
                } else if (ref instanceof final ArrayRef arrayRef) {
                    base = arrayRef.getBase();
                }
            } else if (assignStmt.getRightOp() instanceof final InstanceInvokeExpr invokeExpr) {
                base = invokeExpr.getBase();
            }
        } else if (unit instanceof final InstanceInvokeExpr invokeExpr) {
            base = invokeExpr.getBase();
        }

        if (base != null) {
            final Value condition =
                    Jimple.v().newNeExpr(
                            Vimp.v().nest(base),
                            NullConstant.v()
                    );

            return Optional.of(condition);
        } else {
            return Optional.empty();
        }
    }

}
