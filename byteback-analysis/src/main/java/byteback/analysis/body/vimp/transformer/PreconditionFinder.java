package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
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
