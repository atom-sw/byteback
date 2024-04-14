package byteback.syntax.type.declaration.method.body.tag;

import byteback.syntax.tag.TagProvider;
import byteback.common.function.Lazy;
import soot.Body;

public class InferredLocalFramesProvider extends TagProvider<Body, InferredLocalFramesTag> {

    private static final Lazy<InferredLocalFramesProvider> INSTANCE =
            Lazy.from(()  -> new InferredLocalFramesProvider(InferredLocalFramesTag.NAME));

    public static InferredLocalFramesProvider v() {
        return INSTANCE.get();
    }

    private InferredLocalFramesProvider(final String tagName) {
        super(tagName);
    }

    @Override
    public InferredLocalFramesTag compute(final Body host) {
        return new InferredLocalFramesTag();
    }

}