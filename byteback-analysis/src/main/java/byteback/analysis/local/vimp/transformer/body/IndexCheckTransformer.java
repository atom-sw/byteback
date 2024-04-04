package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.local.vimp.syntax.value.NestedExprConstructor;
import byteback.common.function.Lazy;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;

import java.util.Optional;

/**
 * Introduces explicit index checks before every array dereference.
 *
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
    public Optional<Value> makeUnitCheck(final NestedExprConstructor immediateConstructor, final Unit unit) {
        if (unit instanceof AssignStmt assignStmt && assignStmt.getLeftOp() instanceof ArrayRef arrayRef) {
            final Value indexValue = arrayRef.getIndex();
            final Value arrayBase = arrayRef.getBase();
            final Value condition =
                    Jimple.v().newAndExpr(
                            immediateConstructor.apply(
                                    Jimple.v().newLeExpr(
                                            IntConstant.v(0),
                                            indexValue
                                    )
                            ),
                            immediateConstructor.apply(
                                    Jimple.v().newLtExpr(
                                            indexValue,
                                            immediateConstructor.apply(
                                                    Jimple.v().newLengthExpr(arrayBase)
                                            )
                                    )
                            )
                    );

            return Optional.of(condition);
        }

        return Optional.empty();
    }

}
