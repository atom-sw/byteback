package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTagProvider;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTag;
import soot.Value;

import java.util.ArrayList;
import java.util.List;

public class PostconditionsPropagator extends ConditionsPropagator<PostconditionsTag> {

    private static final Lazy<PostconditionsPropagator> INSTANCE =
            Lazy.from(() -> new PostconditionsPropagator(PostconditionsTagProvider.v()));

    public static PostconditionsPropagator v() {
        return INSTANCE.get();
    }

    private PostconditionsPropagator(final PostconditionsTagProvider conditionsProvider) {
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
