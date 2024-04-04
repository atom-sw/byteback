package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.local.common.transformer.body.BodyTransformer;
import byteback.analysis.local.vimp.syntax.Vimp;
import byteback.analysis.local.vimp.syntax.value.VoidConstant;
import byteback.common.function.Lazy;
import soot.Body;
import soot.Unit;
import soot.Value;
import soot.jimple.Jimple;

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
        final Value condition = Jimple.v().newEqExpr(
                Vimp.v().newCaughtExceptionRef(),
                VoidConstant.v()
        );
        final Unit assumeUnit = Vimp.v().newAssumeStmt(condition);
        body.getUnits().addFirst(assumeUnit);
    }

}
