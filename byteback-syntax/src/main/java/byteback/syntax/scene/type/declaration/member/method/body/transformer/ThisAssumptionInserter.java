package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.tag.ThisLocalTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.body.value.ThisLocal;
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

    private static final Lazy<ThisAssumptionInserter> INSTANCE = Lazy.from(ThisAssumptionInserter::new);

    public static ThisAssumptionInserter v() {
        return INSTANCE.get();
    }

    private ThisAssumptionInserter() {
    }

    @Override
    public void transformBody(final BodyContext bodyContext) {
        final Body body = bodyContext.getBody();

        if (!body.getMethod().isStatic()) {
            final ThisLocal thisLocal = ThisLocalTagAccessor.v()
                    .getOrThrow(body)
                    .getThisLocal();
            final Value condition =
                    Vimp.v().nest(
                            Jimple.v().newNeExpr(
                                    thisLocal,
                                    NullConstant.v()
                            )
                    );
            final Unit assumeUnit = Vimp.v().newAssumeStmt(condition);
            body.getUnits().addFirst(assumeUnit);
        }
    }

}
