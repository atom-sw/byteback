package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagProvider;
import soot.SootMethod;

public class IdentityStmtsTagProvider extends TagProvider<SootMethod, IdentityStmtsTag> {

    private static final Lazy<IdentityStmtsTagProvider> INSTANCE =
            Lazy.from(() -> new IdentityStmtsTagProvider(IdentityStmtsTag.NAME));

    public static IdentityStmtsTagProvider v() {
        return INSTANCE.get();
    }

    private IdentityStmtsTagProvider(final String tagName) {
        super(tagName);
    }

}