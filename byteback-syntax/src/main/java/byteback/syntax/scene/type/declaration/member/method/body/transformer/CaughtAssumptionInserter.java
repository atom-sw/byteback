package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.VoidConstant;
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

    private static final Lazy<CaughtAssumptionInserter> INSTANCE = Lazy.from(CaughtAssumptionInserter::new);

    public static CaughtAssumptionInserter v() {
        return INSTANCE.get();
    }

    private CaughtAssumptionInserter() {
    }

    @Override
    public void transformBody(final BodyContext bodyContext) {
        final Body body = bodyContext.getBody();
        final Value condition =
                Vimp.v().nest(
                        Jimple.v().newEqExpr(
                                Vimp.v().newCaughtExceptionRef(),
                                VoidConstant.v()
                        )
                );
        final Unit assumeUnit = Vimp.v().newAssumeStmt(condition);
        body.getUnits().addFirst(assumeUnit);
    }

}
