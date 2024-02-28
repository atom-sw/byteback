package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.vimp.Vimp;
import byteback.common.Lazy;

import java.util.Map;

import soot.*;
import soot.grimp.Grimp;
import soot.grimp.GrimpBody;
import soot.jimple.*;
import soot.options.Options;

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
            if (!body.getMethod().isStatic()) {
                final Unit unit = body.getThisUnit();
                final Unit assumption = Vimp.v()
                        .newAssumptionStmt(Vimp.v().newNeExpr(body.getThisLocal(), NullConstant.v()));
                body.getUnits().insertAfter(assumption, unit);
            }

            super.internalTransform(body, phaseName, options);
        }
    }

    @Override
    public Value extractTarget(final Value value) {
        Value target = null;

        if (value instanceof NewExpr || value instanceof SpecialInvokeExpr) {
            return null;
        }

        if (value instanceof InstanceInvokeExpr invokeExpr) {
            target = invokeExpr.getBase();
        }

        if (value instanceof InstanceFieldRef fieldRef) {
            target = fieldRef.getBase();
        }

        if (value instanceof ArrayRef arrayRef) {
            target = arrayRef.getBase();
        }

        if (value instanceof LengthExpr lengthExpr) {
            target = lengthExpr.getOp();
        }

        if (target instanceof ThisRef) {
            return null;
        }

        return target;
    }

    @Override
    public Value createCheckExpr(Value target, Value outer)
    {
        return Grimp.v().newNeExpr(target, NullConstant.v());
    }

}
