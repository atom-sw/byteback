package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import soot.SootMethod;

public class PreconditionsProvider extends ConditionsProvider<PreconditionsTag> {

    private static final Lazy<PreconditionsProvider> INSTANCE =
            Lazy.from(()  -> new PreconditionsProvider(PreconditionsTag.NAME));

    public static PreconditionsProvider v() {
        return INSTANCE.get();
    }

    private PreconditionsProvider(final String tagName) {
        super(tagName);
    }

    @Override
    public PreconditionsTag compute(final SootMethod sootMethod) {
        return new PreconditionsTag();
    }

}