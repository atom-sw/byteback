package byteback.syntax.scene.type.declaration.member.method.body.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagAccessor;
import soot.Body;

public class ArgumentLocalsTagAccessor extends TagAccessor<Body, ArgumentLocalsTag> {

    private static final Lazy<ArgumentLocalsTagAccessor> INSTANCE =
            Lazy.from(() -> new ArgumentLocalsTagAccessor(ArgumentLocalsTag.NAME));

    public static ArgumentLocalsTagAccessor v() {
        return INSTANCE.get();
    }

    private ArgumentLocalsTagAccessor(final String tagName) {
        super(tagName);
    }

}