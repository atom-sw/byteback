package byteback.syntax.member.method.transformer;

import byteback.syntax.member.method.tag.PostconditionsProvider;
import byteback.syntax.member.method.tag.PostconditionsTag;
import byteback.syntax.value.box.ConditionExprBox;
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
