package byteback.analysis.global.vimp.transformer;

import byteback.analysis.global.vimp.tag.PostconditionsProvider;
import byteback.analysis.global.vimp.tag.PostconditionsTag;
import byteback.analysis.local.vimp.syntax.value.box.ConditionExprBox;
import byteback.common.function.Lazy;

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
    public void combineConditions(final List<ConditionExprBox> originalPostconditionBoxes,
                                  final List<ConditionExprBox> overridingPostconditionBoxes) {
        overridingPostconditionBoxes.addAll(originalPostconditionBoxes);
    }

}
