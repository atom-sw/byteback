package byteback.analysis.local.vimp.tag.body;

import byteback.analysis.common.tag.TagProvider;
import byteback.common.function.Lazy;
import soot.Body;

import java.util.ArrayList;

public class InferredLocalFramesProvider extends TagProvider<Body, InferredLocalFramesTag> {

    private static final Lazy<InferredLocalFramesProvider> instance =
            Lazy.from(()  -> new InferredLocalFramesProvider(InferredLocalFramesTag.NAME));

    public static InferredLocalFramesProvider v() {
        return instance.get();
    }

    private InferredLocalFramesProvider(final String tagName) {
        super(tagName);
    }

    @Override
    public InferredLocalFramesTag compute(final Body host) {
        return new InferredLocalFramesTag(new ArrayList<>());
    }

}