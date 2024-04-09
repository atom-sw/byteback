package byteback.analysis.global.vimp.transformer;

import byteback.analysis.local.vimp.tag.body.PostconditionsProvider;
import byteback.analysis.local.vimp.tag.body.PostconditionsTag;
import byteback.analysis.local.vimp.tag.body.PreconditionsProvider;
import byteback.analysis.local.vimp.tag.body.PreconditionsTag;
import byteback.common.function.Lazy;
import soot.Value;

import java.util.List;

public class PostconditionsPropagator extends ConditionsPropagator<PostconditionsTag> {

    private static final Lazy<PostconditionsPropagator> instance =
            Lazy.from(() -> new PostconditionsPropagator(PostconditionsProvider.v()));

    public static PostconditionsPropagator v() {
        return instance.get();
    }

    private PostconditionsPropagator(final PostconditionsProvider conditionsProvider) {
        super(conditionsProvider);
    }

    @Override
    public void combineConditions(final List<Value> originalPostconditions,
                                  final List<Value> overridingPostconditions) {

        overridingPostconditions.addAll(originalPostconditions);
    }

}
