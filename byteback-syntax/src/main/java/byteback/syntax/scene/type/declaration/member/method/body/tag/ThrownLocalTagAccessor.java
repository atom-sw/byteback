package byteback.syntax.scene.type.declaration.member.method.body.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagAccessor;
import soot.Body;

public class ThrownLocalTagAccessor extends TagAccessor<Body, ThrownLocalTag> {

    private static final Lazy<ThrownLocalTagAccessor> INSTANCE =
            Lazy.from(() -> new ThrownLocalTagAccessor(ThrownLocalTag.NAME));

    public static ThrownLocalTagAccessor v() {
        return INSTANCE.get();
    }

    private ThrownLocalTagAccessor(final String tagName) {
        super(tagName);
    }

}