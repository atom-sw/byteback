package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.vimp.NestedExprConstructor;
import byteback.analysis.body.vimp.Vimp;
import byteback.common.function.Lazy;
import soot.Body;
import soot.Unit;
import soot.Value;
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
            final Value condition = new NestedExprConstructor(body).make(
                    Vimp.v()::newNeExpr,
                    body.getThisLocal(),
                    NullConstant.v()
            );
            final Unit assumption = Vimp.v().newAssumeStmt(condition);
            body.getUnits().addFirst(assumption);
        }
    }

}
