package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagMarker;
import soot.SootMethod;

/**
 * Flags a method whose invocations should be ignored in the analysis.
 *
 * @author paganma
 */
public class IgnoreTagMarker extends TagMarker<SootMethod, IgnoreTag> {

    private static final Lazy<IgnoreTagMarker> INSTANCE =
            Lazy.from(() -> new IgnoreTagMarker(IgnoreTag.v()));

    public static IgnoreTagMarker v() {
        return INSTANCE.get();
    }

    private IgnoreTagMarker(final IgnoreTag tag) {
        super(tag);
    }

}