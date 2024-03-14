package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.syntax.stmt.Unit;
import byteback.analysis.body.jimple.syntax.expr.ArrayRef;
import byteback.analysis.body.jimple.syntax.expr.InstanceFieldRef;
import byteback.analysis.body.jimple.syntax.expr.InstanceInvokeExpr;
import byteback.analysis.body.jimple.syntax.expr.Ref;
import byteback.analysis.body.jimple.syntax.stmt.AssignStmt;
import byteback.analysis.body.jimple.syntax.stmt.InvokeStmt;
import byteback.analysis.body.vimp.VimpExprFactory;
import byteback.analysis.model.syntax.type.ClassType;
import byteback.common.function.Lazy;
import byteback.analysis.body.common.syntax.expr.Value;

import java.util.Optional;

public class NullCheckTransformer extends CheckTransformer {

    private static final Lazy<NullCheckTransformer> instance = Lazy.from(NullCheckTransformer::new);

    private NullCheckTransformer() {
        super(new ClassType("java.lang.NullPointerException"));
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
            if (invokeStmt.getInvokeExpr() instanceof InstanceInvokeExpr invokeExpr) {
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
