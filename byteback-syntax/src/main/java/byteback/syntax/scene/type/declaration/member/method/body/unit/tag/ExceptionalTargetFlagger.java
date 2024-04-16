package byteback.syntax.scene.type.declaration.member.method.body.unit.tag;

import byteback.syntax.tag.TagFlagger;
import byteback.common.function.Lazy;
import soot.tagkit.Host;

/**
 * Flags units that are targeted by exceptional branches.
 *
 * @author paganma
 */
public class ExceptionalTargetFlagger extends TagFlagger<Host, ExceptionalTargetTag> {

    private static final Lazy<ExceptionalTargetFlagger> instance =
            Lazy.from(()  -> new ExceptionalTargetFlagger(ExceptionalTargetTag.v()));

    public static ExceptionalTargetFlagger v() {
        return instance.get();
    }

    private ExceptionalTargetFlagger(final ExceptionalTargetTag tag) {
        super(tag);
    }

}
