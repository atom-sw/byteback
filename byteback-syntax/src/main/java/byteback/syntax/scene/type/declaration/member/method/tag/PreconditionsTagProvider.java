package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;

public class PreconditionsTagProvider extends ConditionsTagProvider<PreconditionsTag> {

    private static final Lazy<PreconditionsTagProvider> INSTANCE =
            Lazy.from(() -> new PreconditionsTagProvider(PreconditionsTag.NAME));

    public static PreconditionsTagProvider v() {
        return INSTANCE.get();
    }

    private PreconditionsTagProvider(final String tagName) {
        super(tagName);
    }

    @Override
    public PreconditionsTag compute() {
        return new PreconditionsTag();
    }

}