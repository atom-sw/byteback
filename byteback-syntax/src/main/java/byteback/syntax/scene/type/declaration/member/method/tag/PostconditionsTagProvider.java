package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;

public class PostconditionsTagProvider extends ConditionsTagProvider<PostconditionsTag> {

    private static final Lazy<PostconditionsTagProvider> INSTANCE =
            Lazy.from(() -> new PostconditionsTagProvider(PostconditionsTag.NAME));

    public static PostconditionsTagProvider v() {
        return INSTANCE.get();
    }

    private PostconditionsTagProvider(final String tagName) {
        super(tagName);
    }

    @Override
    public PostconditionsTag compute() {
        return new PostconditionsTag();
    }

}