package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.vimp.ImmediateConstructor;
import byteback.analysis.body.vimp.Vimp;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;

import java.util.Optional;

/**
 * Introduces explicit index checks before every array dereference.
 * @author paganma
 */
public class IndexCheckTransformer extends CheckTransformer {

    private static final Lazy<IndexCheckTransformer> instance = Lazy.from(IndexCheckTransformer::new);

    private IndexCheckTransformer() {
        super("java.lang.IndexOutOfBoundsException");
    }

    public static IndexCheckTransformer v() {
        return instance.get();
    }

    @Override
    public Optional<Value> makeUnitCheck(final ImmediateConstructor checkConstructor, final Unit unit) {
        if (unit instanceof AssignStmt assignStmt && assignStmt.getLeftOp() instanceof ArrayRef arrayRef) {
            final Value indexValue = arrayRef.getIndex();
            final Value arrayBase = arrayRef.getBase();
            final Value conditionExpr =
                    checkConstructor.make(Vimp.v()::newLogicAndExpr,
                            checkConstructor.make(
                                    Vimp.v()::newLeExpr,
                                    IntConstant.v(0),
                                    indexValue
                            ),
                            checkConstructor.make(
                                    Vimp.v()::newLtExpr,
                                    indexValue,
                                    Jimple.v().newLengthExpr(arrayBase)
                            )
                    );

            return Optional.of(conditionExpr);
        }

        return Optional.empty();
    }

}
