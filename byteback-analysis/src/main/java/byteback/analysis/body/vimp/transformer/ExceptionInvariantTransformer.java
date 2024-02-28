package byteback.analysis.body.vimp.transformer;

import byteback.common.Lazy;

import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
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
    public void internalTransform(final Body b, final String phaseName, final Map<String, String> options) {
        final LoopFinder loopFinder = new LoopFinder();
        final Set<Loop> loops = loopFinder.getLoops(b);
    }

}
