package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.syntax.stmt.Unit;
import byteback.analysis.body.jimple.syntax.expr.ArrayRef;
import byteback.analysis.body.jimple.syntax.expr.IntConstant;
import byteback.analysis.body.jimple.syntax.expr.LengthExpr;
import byteback.analysis.body.jimple.syntax.stmt.AssignStmt;
import byteback.analysis.body.vimp.VimpExprFactory;
import byteback.analysis.model.syntax.type.ClassType;
import byteback.common.function.Lazy;
import byteback.analysis.body.common.syntax.expr.Value;

import java.util.Optional;

public class IndexCheckTransformer extends CheckTransformer {

    private static final Lazy<IndexCheckTransformer> instance = Lazy.from(IndexCheckTransformer::new);

    private IndexCheckTransformer() {
        super(new ClassType("java.lang.IndexOutOfBoundsException"));
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
                    builder.binary(AndExpr::new,
                            builder.binary(
                                    LeExpr::new,
                                    IntConstant.v(0),
                                    indexValue),
                            builder.binary(
                                    LtExpr::new,
                                    indexValue,
                                    new LengthExpr(arrayBase)));

            return Optional.of(conditionExpr);
        }

        return Optional.empty();
    }

}
