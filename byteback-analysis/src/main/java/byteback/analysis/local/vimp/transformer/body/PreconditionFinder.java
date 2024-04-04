package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.local.common.transformer.body.BodyTransformer;
import byteback.common.function.Lazy;
import soot.Body;

public class PreconditionFinder extends BodyTransformer {

    private final static Lazy<PreconditionFinder> instance = Lazy.from(PreconditionFinder::new);

    public static PreconditionFinder v() {
        return instance.get();
    }

    private PreconditionFinder() {
    }

    @Override
    public void transformBody(final Body body) {
    }

}
