package byteback.syntax.type.declaration.method.transformer;

import byteback.syntax.type.declaration.method.tag.PreconditionsProvider;
import byteback.syntax.type.declaration.method.tag.PreconditionsTag;
import byteback.common.function.Lazy;
import soot.Value;

import java.util.List;

public class PreconditionsPropagator extends ConditionsPropagator<PreconditionsTag> {

    private static final Lazy<PreconditionsPropagator> INSTANCE = Lazy.from(() ->
            new PreconditionsPropagator(PreconditionsProvider.v()));

    public static PreconditionsPropagator v() {
        return INSTANCE.get();
    }

    public PreconditionsPropagator(PreconditionsProvider conditionsProvider) {
        super(conditionsProvider);
    }

    @Override
    public void combineConditions(final List<Value> originalPreconditionBoxes,
                                  final List<Value> overridingPreconditionBoxes) {
        // TODO
    }

}
