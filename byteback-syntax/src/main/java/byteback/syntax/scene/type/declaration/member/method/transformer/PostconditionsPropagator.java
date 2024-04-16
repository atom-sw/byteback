package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsProvider;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTag;
import soot.Value;

import java.util.ArrayList;
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
    public List<Value> combineConditions(final List<Value> originalConditions,
                                         final List<Value> overridingConditions) {
        final var combinedConditions = new ArrayList<Value>(overridingConditions);
        combinedConditions.addAll(originalConditions);

        return combinedConditions;
    }

}
