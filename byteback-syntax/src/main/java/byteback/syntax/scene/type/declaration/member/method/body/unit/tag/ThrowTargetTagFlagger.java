package byteback.syntax.scene.type.declaration.member.method.body.unit.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagFlagger;
import soot.tagkit.Host;

/**
 * Flags units that are targeted by exceptional branches.
 *
 * @author paganma
 */
public class ThrowTargetTagFlagger extends TagFlagger<Host, ThrowTargetTag> {

    private static final Lazy<ThrowTargetTagFlagger> instance =
            Lazy.from(() -> new ThrowTargetTagFlagger(ThrowTargetTag.v()));

    public static ThrowTargetTagFlagger v() {
        return instance.get();
    }

    private ThrowTargetTagFlagger(final ThrowTargetTag tag) {
        super(tag);
    }

}
