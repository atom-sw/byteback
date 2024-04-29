package byteback.syntax.scene.type.declaration.member.method.body.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagAccessor;
import soot.Body;

public class InferredFramesTagAccessor extends TagAccessor<Body, InferredFramesTag> {

    private static final Lazy<InferredFramesTagAccessor> INSTANCE =
            Lazy.from(() -> new InferredFramesTagAccessor(InferredFramesTag.NAME));

    public static InferredFramesTagAccessor v() {
        return INSTANCE.get();
    }

    private InferredFramesTagAccessor(final String tagName) {
        super(tagName);
    }

}