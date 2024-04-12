package byteback.syntax.member.method.tag;

import byteback.common.function.Lazy;
import soot.SootMethod;

import java.util.ArrayList;

public class PreconditionsProvider extends ConditionsProvider<PreconditionsTag> {

    private static final Lazy<PreconditionsProvider> instance =
            Lazy.from(()  -> new PreconditionsProvider(PreconditionsTag.NAME));

    public static PreconditionsProvider v() {
        return instance.get();
    }

    private PreconditionsProvider(final String tagName) {
        super(tagName);
    }

    @Override
    public PreconditionsTag compute(final SootMethod sootMethod) {
        return new PreconditionsTag(new ArrayList<>());
    }

}