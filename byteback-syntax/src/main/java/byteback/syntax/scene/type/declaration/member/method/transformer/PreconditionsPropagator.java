package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTagProvider;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTag;
import soot.Value;
import soot.jimple.Jimple;

import java.util.ArrayList;
import java.util.List;

public class PreconditionsPropagator extends ConditionsPropagator<PreconditionsTag> {

    private static final Lazy<PreconditionsPropagator> INSTANCE = Lazy.from(() ->
            new PreconditionsPropagator(PreconditionsTagProvider.v()));

    public static PreconditionsPropagator v() {
        return INSTANCE.get();
    }

    public PreconditionsPropagator(final PreconditionsTagProvider conditionsProvider) {
        super(conditionsProvider);
    }

    @Override
    public List<Value> combineConditions(final List<Value> originalConditions,
                                         final List<Value> overridingConditions) {
        final var combinedConditions = new ArrayList<Value>();

        for (final Value originalCondition : originalConditions) {
            for (final Value overridingCondition : overridingConditions) {
                final Value newCondition = Jimple.v().newOrExpr(
                        Vimp.v().nest(overridingCondition),
                        Vimp.v().nest(originalCondition)
                );
                combinedConditions.add(newCondition);
            }
        }

        return combinedConditions;
    }

}
