package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.VoidConstant;
import byteback.common.Lazy;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.Trap;
import soot.Unit;
import soot.Value;
import soot.grimp.GrimpBody;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.util.Chain;

public class ExceptionInvariantTransformer extends BodyTransformer {

    private static final Lazy<ExceptionInvariantTransformer> instance = Lazy.from(ExceptionInvariantTransformer::new);

    private ExceptionInvariantTransformer() {
    }

    public static ExceptionInvariantTransformer v() {
        return instance.get();
    }

    @Override
    public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        transformBody(body);
    }

    public void transformBody(final Body b) {
        final LoopFinder loopFinder = new LoopFinder();
        final Set<Loop> loops = loopFinder.getLoops(b);
    }
}
