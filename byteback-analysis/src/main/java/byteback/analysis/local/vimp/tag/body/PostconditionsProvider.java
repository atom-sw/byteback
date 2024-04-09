package byteback.analysis.local.vimp.tag.body;

import byteback.analysis.common.tag.TagProvider;
import byteback.common.function.Lazy;
import soot.Body;

import java.util.ArrayList;

public class PostconditionsProvider extends TagProvider<Body, PreconditionsTag> {

    private static final Lazy<PostconditionsProvider> instance =
            Lazy.from(()  -> new PostconditionsProvider(InferredLocalFramesTag.NAME));

    public static PostconditionsProvider v() {
        return instance.get();
    }

    private PostconditionsProvider(final String tagName) {
        super(tagName);
    }

    @Override
    public PreconditionsTag compute(final Body body) {
        return new PreconditionsTag(new ArrayList<>());
    }

}