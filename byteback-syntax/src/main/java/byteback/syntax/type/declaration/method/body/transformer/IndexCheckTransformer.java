package byteback.syntax.type.declaration.method.body.transformer;

import byteback.syntax.Vimp;
import soot.Scene;
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

    public IndexCheckTransformer(final Scene scene) {
        super(scene, "java.lang.IndexOutOfBoundsException");
    }

    @Override
    public Optional<Value> makeUnitCheck(final Unit unit) {
        if (unit instanceof AssignStmt assignStmt && assignStmt.getLeftOp() instanceof ArrayRef arrayRef) {
            final Value indexValue = arrayRef.getIndex();
            final Value arrayBase = arrayRef.getBase();
            final Value condition =
                    Jimple.v().newAndExpr(
                            Vimp.v().nest(
                                    Jimple.v().newLeExpr(
                                            IntConstant.v(0),
                                            indexValue
                                    )
                            ),
                            Vimp.v().nest(
                                    Jimple.v().newLtExpr(
                                            indexValue,
                                            Vimp.v().nest(
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
