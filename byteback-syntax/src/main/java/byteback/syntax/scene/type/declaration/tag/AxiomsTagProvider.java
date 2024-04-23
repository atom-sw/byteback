package byteback.syntax.scene.type.declaration.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.LazyTagProvider;
import soot.SootClass;

public class AxiomsTagProvider extends LazyTagProvider<SootClass, AxiomsTag> {

    private static final Lazy<AxiomsTagProvider> INSTANCE =
            Lazy.from(() -> new AxiomsTagProvider(AxiomsTag.NAME));

    private AxiomsTagProvider(final String tagName) {
        super(tagName);
    }

    public static AxiomsTagProvider v() {
        return INSTANCE.get();
    }

    @Override
    public AxiomsTag compute() {
        return new AxiomsTag();
    }

}