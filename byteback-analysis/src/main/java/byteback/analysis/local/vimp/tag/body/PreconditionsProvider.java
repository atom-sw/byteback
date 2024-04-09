package byteback.analysis.local.vimp.tag.body;

import byteback.analysis.common.tag.TagProvider;
import byteback.common.function.Lazy;
import soot.Body;

import java.util.ArrayList;

public class PreconditionsProvider extends TagProvider<Body, PreconditionsTag> {

    private static final Lazy<PreconditionsProvider> instance =
            Lazy.from(()  -> new PreconditionsProvider(PreconditionsTag.NAME));

    public static PreconditionsProvider v() {
        return instance.get();
    }

    private PreconditionsProvider(final String tagName) {
        super(tagName);
    }

    @Override
    public PreconditionsTag compute(final Body body) {
        return new PreconditionsTag(new ArrayList<>());
    }

}