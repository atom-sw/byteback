package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.local.common.transformer.body.BodyTransformer;
import byteback.analysis.local.vimp.syntax.Vimp;
import byteback.common.function.Lazy;
import soot.Body;
import soot.Unit;
import soot.Value;
import soot.jimple.Jimple;
import soot.jimple.NullConstant;

/**
 * Explicitly introduces the basic assumption that @this != null.
 *
 * @author paganma
 */
public class ThisAssumptionInserter extends BodyTransformer {

    private static final Lazy<ThisAssumptionInserter> instance = Lazy.from(ThisAssumptionInserter::new);

    public static ThisAssumptionInserter v() {
        return instance.get();
    }

    private ThisAssumptionInserter() {
    }

    @Override
    public void transformBody(final Body body) {
        if (!body.getMethod().isStatic()) {
            final Value condition =
                    Jimple.v().newNeExpr(
                            body.getThisLocal(),
                            NullConstant.v()
                    );
            final Unit assumption = Vimp.v().newAssumeStmt(condition);
            body.getUnits().addFirst(assumption);
        }
    }

}