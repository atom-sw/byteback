package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.VimpExprFactory;
import byteback.common.function.Lazy;
import soot.Scene;
import soot.Unit;
import soot.Value;
import soot.jimple.*;

import java.util.Optional;

public class NullCheckTransformer extends CheckTransformer {

    private static final Lazy<NullCheckTransformer> instance = Lazy.from(NullCheckTransformer::new);

    private NullCheckTransformer() {
        super(Scene.v().loadClassAndSupport("java.lang.NullPointerException"));
    }

    public static NullCheckTransformer v() {
        return instance.get();
    }

    @Override
    public Optional<Value> makeUnitCheck(final VimpExprFactory factory, final Unit unit) {
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
            final Value conditionExpr = factory.binary(
                    Vimp.v()::newNeExpr,
                    base,
                    NullConstant.v()
            );

            return Optional.of(conditionExpr);
        } else {
            return Optional.empty();
        }
    }

}
