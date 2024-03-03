package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.vimp.NestedExprFactory;
import byteback.analysis.body.vimp.Vimp;
import byteback.common.function.Lazy;

import java.util.Map;
import java.util.Optional;

import soot.*;
import soot.jimple.*;

public class NullCheckTransformer extends CheckTransformer {

    private static final Lazy<NullCheckTransformer> instance = Lazy.from(NullCheckTransformer::new);

    private NullCheckTransformer() {
        super(Scene.v().loadClassAndSupport("java.lang.NullPointerException"));
    }

    public static NullCheckTransformer v() {
        return instance.get();
    }

    @Override
    public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        if (PhaseOptions.getBoolean(options, "enabled")) {
            super.internalTransform(body, phaseName, options);
        }
    }

    @Override
    public Optional<Value> makeUnitCheck(final NestedExprFactory factory, final Unit unit) {
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
                    NullConstant.v());

            return Optional.of(conditionExpr);
        } else {
            return Optional.empty();
        }
    }

}
