package byteback.syntax.scene.type.declaration.member.method.body.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagAccessor;
import soot.Body;

public class InstanceRefTagAccessor extends TagAccessor<Body, InstanceRefTag> {

    private static final Lazy<InstanceRefTagAccessor> INSTANCE =
            Lazy.from(() -> new InstanceRefTagAccessor(InstanceRefTag.NAME));

    public static InstanceRefTagAccessor v() {
        return INSTANCE.get();
    }

    private InstanceRefTagAccessor(final String tagName) {
        super(tagName);
    }

}