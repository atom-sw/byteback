package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagAccessor;
import soot.SootMethod;

public class PreludeTagAccessor extends TagAccessor<SootMethod, PreludeTag> {

    private static final Lazy<PreludeTagAccessor> INSTANCE =
            Lazy.from(() -> new PreludeTagAccessor(PreludeTag.NAME));

    public static PreludeTagAccessor v() {
        return INSTANCE.get();
    }

    private PreludeTagAccessor(final String tagName) {
        super(tagName);
    }

}