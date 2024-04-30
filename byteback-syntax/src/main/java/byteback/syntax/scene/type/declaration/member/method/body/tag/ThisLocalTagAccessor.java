package byteback.syntax.scene.type.declaration.member.method.body.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagAccessor;
import soot.Body;

public class ThisLocalTagAccessor extends TagAccessor<Body, ThisLocalTag> {

    private static final Lazy<ThisLocalTagAccessor> INSTANCE =
            Lazy.from(() -> new ThisLocalTagAccessor(ThisLocalTag.NAME));

    public static ThisLocalTagAccessor v() {
        return INSTANCE.get();
    }

    private ThisLocalTagAccessor(final String tagName) {
        super(tagName);
    }

}