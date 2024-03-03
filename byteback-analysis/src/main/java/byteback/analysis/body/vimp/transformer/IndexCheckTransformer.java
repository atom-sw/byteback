package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.vimp.NestedExprFactory;
import byteback.analysis.body.vimp.Vimp;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;

import java.util.Map;
import java.util.Optional;

public class IndexCheckTransformer extends CheckTransformer {

    private static final Lazy<IndexCheckTransformer> instance = Lazy.from(IndexCheckTransformer::new);

    private IndexCheckTransformer() {
        super(Scene.v().loadClassAndSupport("java.lang.IndexOutOfBoundsException"));
    }

    public static IndexCheckTransformer v() {
        return instance.get();
    }

    @Override
    public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        if (PhaseOptions.getBoolean(options, "enabled")) {
            super.internalTransform(body, phaseName, options);
        }
    }

    @Override
    public Optional<Value> makeUnitCheck(final NestedExprFactory builder, final Unit unit) {
        if (unit instanceof AssignStmt assignStmt && assignStmt.getLeftOp() instanceof ArrayRef arrayRef) {
            final Value indexValue = arrayRef.getIndex();
            final Value arrayBase = arrayRef.getBase();
            final Value conditionExpr =
                    builder.binary(Vimp.v()::newLogicAndExpr,
                            builder.binary(
                                    Vimp.v()::newLeExpr,
                                    IntConstant.v(0),
                                    indexValue),
                            builder.binary(
                                    Vimp.v()::newLtExpr,
                                    indexValue,
                                    Jimple.v().newLengthExpr(arrayBase)));

            return Optional.of(conditionExpr);
        }

        return Optional.empty();
    }

}
