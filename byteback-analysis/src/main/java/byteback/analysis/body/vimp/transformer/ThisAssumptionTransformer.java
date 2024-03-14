package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.syntax.stmt.Unit;
import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.vimp.VimpExprFactory;
import byteback.analysis.body.vimp.syntax.AssumeStmt;
import byteback.analysis.model.Modifier;
import byteback.common.function.Lazy;
import byteback.analysis.body.common.syntax.Body;
import byteback.analysis.body.common.syntax.expr.Value;

public class ThisAssumptionTransformer extends BodyTransformer {

    private static final Lazy<ThisAssumptionTransformer> instance = Lazy.from(ThisAssumptionTransformer::new);

    public static ThisAssumptionTransformer v() {
        return instance.get();
    }

    private ThisAssumptionTransformer() {
    }

    @Override
    public void transformBody(final Body body) {
        if (!Modifier.isStatic(body.getMethodModel().getModifiers())) {
            final Unit unit = body.getThisUnit();
            final Value condition = new VimpExprFactory(body).binary(
                    NeExpr::new,
                    body.getThisLocal(),
                    NullConstant.v()
            );
            final Unit assumption = new AssumeStmt(condition);
            body.getUnits().insertAfter(assumption, unit);
        }
    }

}
