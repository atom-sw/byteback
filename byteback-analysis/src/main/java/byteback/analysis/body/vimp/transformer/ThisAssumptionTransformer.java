package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.VimpExprFactory;
import byteback.common.function.Lazy;
import soot.Body;
import soot.Unit;
import soot.Value;
import soot.jimple.NullConstant;

public class ThisAssumptionTransformer extends BodyTransformer {

    private static final Lazy<ThisAssumptionTransformer> instance = Lazy.from(ThisAssumptionTransformer::new);

    public static ThisAssumptionTransformer v() {
        return instance.get();
    }

    private ThisAssumptionTransformer() {
    }

    @Override
    public void transformBody(final Body body) {
        if (!body.getMethod().isStatic()) {
            final Unit unit = body.getThisUnit();
            final Value condition = new VimpExprFactory(body).binary(
                    Vimp.v()::newNeExpr,
                    body.getThisLocal(),
                    NullConstant.v()
            );
            final Unit assumption = Vimp.v().newAssumeStmt(condition);
            body.getUnits().insertAfter(assumption, unit);
        }
    }

}
