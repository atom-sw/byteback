package byteback.syntax.type.declaration.method.body.transformer;

import byteback.syntax.value.NestedExprConstructor;
import byteback.common.function.Lazy;

import java.util.Optional;

import soot.*;
import soot.jimple.*;

/**
 * Introduces explicit index checks before every array dereference.
 *
 * @author paganma
 */
public class NullCheckTransformer extends CheckTransformer {

    private static final Lazy<NullCheckTransformer> INSTANCE = Lazy.from(NullCheckTransformer::new);

    private NullCheckTransformer() {
        super("java.lang.NullPointerException");
    }

    public static NullCheckTransformer v() {
        return INSTANCE.get();
    }

    @Override
    public Optional<Value> makeUnitCheck(final NestedExprConstructor immediateConstructor, final Unit unit) {
        Value base = null;

        if (unit instanceof final AssignStmt assignStmt) {
            if (assignStmt.getLeftOp() instanceof Ref ref) {
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
                        immediateConstructor.apply(base),
                        NullConstant.v()
                    );

            return Optional.of(condition);
        } else {
            return Optional.empty();
        }
    }

}
