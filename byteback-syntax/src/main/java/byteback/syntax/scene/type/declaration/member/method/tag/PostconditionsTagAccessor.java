package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;

public class PostconditionsTagAccessor extends ConditionsTagAccessor<PostconditionsTag> {

    private static final Lazy<PostconditionsTagAccessor> INSTANCE =
            Lazy.from(() -> new PostconditionsTagAccessor(PostconditionsTag.NAME));

    public static PostconditionsTagAccessor v() {
        return INSTANCE.get();
    }

    private PostconditionsTagAccessor(final String tagName) {
        super(tagName);
    }

}