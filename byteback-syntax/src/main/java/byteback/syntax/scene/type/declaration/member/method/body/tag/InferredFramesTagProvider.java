package byteback.syntax.scene.type.declaration.member.method.body.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagProvider;
import soot.Body;

public class InferredFramesTagProvider extends TagProvider<Body, InferredFramesTag> {

    private static final Lazy<InferredFramesTagProvider> INSTANCE =
            Lazy.from(() -> new InferredFramesTagProvider(InferredFramesTag.NAME));

    public static InferredFramesTagProvider v() {
        return INSTANCE.get();
    }

    private InferredFramesTagProvider(final String tagName) {
        super(tagName);
    }

}