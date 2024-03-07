package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.common.function.Lazy;

import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;

public class ExceptionInvariantTransformer extends BodyTransformer {

    private static final Lazy<ExceptionInvariantTransformer> instance = Lazy.from(ExceptionInvariantTransformer::new);

    private ExceptionInvariantTransformer() {
    }

    public static ExceptionInvariantTransformer v() {
        return instance.get();
    }

    @Override
    public void transformBody(final Body body) {
        final LoopFinder loopFinder = new LoopFinder();
        final Set<Loop> loops = loopFinder.getLoops(body);
    }

}