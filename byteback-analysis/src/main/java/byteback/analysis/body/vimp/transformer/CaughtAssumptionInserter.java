package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.vimp.NestedExprConstructor;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.syntax.VoidConstant;
import byteback.common.function.Lazy;
import soot.Body;
import soot.Unit;
import soot.Value;

/**
 * Explicitly introduces the basic assumption that @Caught != null.
 *
 * @author paganma
 */
public class CaughtAssumptionInserter extends BodyTransformer {

    private static final Lazy<CaughtAssumptionInserter> instance = Lazy.from(CaughtAssumptionInserter::new);

    public static CaughtAssumptionInserter v() {
        return instance.get();
    }

    private CaughtAssumptionInserter() {
    }

    @Override
    public void transformBody(final Body body) {
        final Value condition = new NestedExprConstructor(body).make(
                Vimp.v()::newEqExpr,
                Vimp.v().newCaughtExceptionRef(),
                VoidConstant.v()
        );
        final Unit assertUnit = Vimp.v().newAssertStmt(condition);
        body.getUnits().addFirst(assertUnit);
    }

}
