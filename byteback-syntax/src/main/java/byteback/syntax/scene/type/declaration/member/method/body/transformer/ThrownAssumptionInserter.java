package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.tag.ThrownLocalTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.body.value.UnitConstant;
import soot.Body;
import soot.Unit;
import soot.Value;
import soot.jimple.Jimple;

/**
 * Explicitly introduces the basic assumption that @Caught != null.
 *
 * @author paganma
 */
public class ThrownAssumptionInserter extends BodyTransformer {

    private static final Lazy<ThrownAssumptionInserter> INSTANCE = Lazy.from(ThrownAssumptionInserter::new);

    public static ThrownAssumptionInserter v() {
        return INSTANCE.get();
    }

    private ThrownAssumptionInserter() {
    }

    @Override
    public void transformBody(final BodyContext bodyContext) {
        final Body body = bodyContext.getBody();

        ThrownLocalTagAccessor.v().get(body).ifPresent(thrownLocalTag -> {
            final Value condition =
                    Vimp.v().nest(
                            Jimple.v().newEqExpr(
                                    thrownLocalTag.getThrownLocal(),
                                    UnitConstant.v()
                            )
                    );
            final Unit assumeUnit = Vimp.v().newAssumeStmt(condition);
            body.getUnits().addFirst(assumeUnit);
        });
    }

}
