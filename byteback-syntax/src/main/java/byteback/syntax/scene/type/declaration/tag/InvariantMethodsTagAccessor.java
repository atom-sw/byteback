package byteback.syntax.scene.type.declaration.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagAccessor;
import soot.SootClass;

public class InvariantMethodsTagAccessor extends TagAccessor<SootClass, InvariantMethodsTag> {

    private static final Lazy<InvariantMethodsTagAccessor> INSTANCE =
            Lazy.from(() -> new InvariantMethodsTagAccessor(InvariantMethodsTag.NAME));

    private InvariantMethodsTagAccessor(final String tagName) {
        super(tagName);
    }

    public static InvariantMethodsTagAccessor v() {
        return INSTANCE.get();
    }

}
