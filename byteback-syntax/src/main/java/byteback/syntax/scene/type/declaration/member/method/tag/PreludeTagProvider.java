package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagProvider;
import soot.SootMethod;

public class PreludeTagProvider extends TagProvider<SootMethod, PreludeTag> {

    private static final Lazy<PreludeTagProvider> INSTANCE =
            Lazy.from(() -> new PreludeTagProvider(PreludeTag.NAME));

    public static PreludeTagProvider v() {
        return INSTANCE.get();
    }

    private PreludeTagProvider(final String tagName) {
        super(tagName);
    }

}