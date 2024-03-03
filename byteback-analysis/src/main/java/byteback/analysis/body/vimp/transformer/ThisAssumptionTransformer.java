package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.vimp.NestedExprFactory;
import byteback.analysis.body.vimp.Vimp;
import byteback.common.function.Lazy;
import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.Value;
import soot.jimple.NullConstant;

import java.util.Map;

public class ThisAssumptionTransformer extends BodyTransformer {

    private static final Lazy<ThisAssumptionTransformer> instance = Lazy.from(ThisAssumptionTransformer::new);

    public static ThisAssumptionTransformer v() {
        return instance.get();
    }

    private ThisAssumptionTransformer() {
    }

    @Override
    protected void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        if (!body.getMethod().isStatic()) {
            final Unit unit = body.getThisUnit();
            final Value condition = new NestedExprFactory(body).binary(
                    Vimp.v()::newNeExpr,
                    body.getThisLocal(),
                    NullConstant.v());
            final Unit assumption = Vimp.v().newAssumeStmt(condition);
            body.getUnits().insertAfter(assumption, unit);
        }
    }

}
