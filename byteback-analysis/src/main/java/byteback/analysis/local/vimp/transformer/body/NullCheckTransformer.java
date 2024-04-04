package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.local.vimp.syntax.value.NestedExprConstructor;
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

    private static final Lazy<NullCheckTransformer> instance = Lazy.from(NullCheckTransformer::new);

    private NullCheckTransformer() {
        super("java.lang.NullPointerException");
    }

    public static NullCheckTransformer v() {
        return instance.get();
    }

    @Override
    public Optional<Value> makeUnitCheck(final NestedExprConstructor immediateConstructor, final Unit unit) {
        Value base = null;

        if (unit instanceof AssignStmt assignStmt) {
            if (assignStmt.getLeftOp() instanceof Ref ref) {
                if (ref instanceof InstanceFieldRef instanceFieldRef) {
                    base = instanceFieldRef.getBase();
                } else if (ref instanceof ArrayRef arrayRef) {
                    base = arrayRef.getBase();
                }
            } else if (assignStmt.getRightOp() instanceof InstanceInvokeExpr invokeExpr) {
                base = invokeExpr.getBase();
            }
        } else if (unit instanceof InvokeStmt invokeStmt) {
            if (invokeStmt instanceof InstanceInvokeExpr invokeExpr) {
                base = invokeExpr.getBase();
            }
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
