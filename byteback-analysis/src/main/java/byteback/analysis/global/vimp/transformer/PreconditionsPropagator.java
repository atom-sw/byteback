package byteback.analysis.global.vimp.transformer;

import byteback.analysis.local.vimp.tag.body.PreconditionsProvider;
import byteback.analysis.local.vimp.tag.body.PreconditionsTag;
import byteback.common.function.Lazy;
import soot.Value;

import java.util.List;

public class PreconditionsPropagator extends ConditionsPropagator<PreconditionsTag> {

    private static final Lazy<PreconditionsPropagator> instance = Lazy.from(() ->
            new PreconditionsPropagator(PreconditionsProvider.v()));

    public static PreconditionsPropagator v() {
        return instance.get();
    }

    public PreconditionsPropagator(PreconditionsProvider conditionsProvider) {
        super(conditionsProvider);
    }

    @Override
    public void combineConditions(final List<Value> originalPreconditions, final List<Value> overridingPreconditions) {
        for (final Value originalCondition : originalPreconditions) {
            for (final Value overridingCondition : overridingPreconditions) {
                // originalCondition || overridingCondition
            }
        }
    }

}
