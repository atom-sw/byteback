package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.syntax.Unit;
import byteback.analysis.body.jimple.syntax.expr.ArrayRef;
import byteback.analysis.body.jimple.syntax.stmt.AssignStmt;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.VimpExprFactory;
import byteback.common.function.Lazy;
import soot.Scene;
import byteback.analysis.body.jimple.syntax.Unit;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.jimple.syntax.ArrayRef;
import byteback.analysis.body.jimple.syntax.AssignStmt;
import byteback.analysis.body.jimple.syntax.IntConstant;
import byteback.analysis.body.jimple.syntax.Jimple;

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
    public Optional<Value> makeUnitCheck(final VimpExprFactory builder, final Unit unit) {
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
