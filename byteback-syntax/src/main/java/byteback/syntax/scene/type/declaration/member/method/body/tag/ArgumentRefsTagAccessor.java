package byteback.syntax.scene.type.declaration.member.method.body.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagAccessor;
import soot.Body;

public class ArgumentRefsTagAccessor extends TagAccessor<Body, ArgumentRefsTag> {

    private static final Lazy<ArgumentRefsTagAccessor> INSTANCE =
            Lazy.from(() -> new ArgumentRefsTagAccessor(ArgumentRefsTag.NAME));

    public static ArgumentRefsTagAccessor v() {
        return INSTANCE.get();
    }

    private ArgumentRefsTagAccessor(final String tagName) {
        super(tagName);
    }

}