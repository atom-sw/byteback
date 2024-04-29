package byteback.syntax.scene.type.declaration.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagAccessor;
import soot.SootClass;

public class AxiomsTagAccessor extends TagAccessor<SootClass, AxiomsTag> {

    private static final Lazy<AxiomsTagAccessor> INSTANCE =
            Lazy.from(() -> new AxiomsTagAccessor(AxiomsTag.NAME));

    private AxiomsTagAccessor(final String tagName) {
        super(tagName);
    }

    public static AxiomsTagAccessor v() {
        return INSTANCE.get();
    }

}