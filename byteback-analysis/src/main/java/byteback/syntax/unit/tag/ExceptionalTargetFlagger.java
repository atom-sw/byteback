package byteback.syntax.unit.tag;

import byteback.syntax.tag.TagFlagger;
import byteback.common.function.Lazy;
import soot.tagkit.Host;

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
