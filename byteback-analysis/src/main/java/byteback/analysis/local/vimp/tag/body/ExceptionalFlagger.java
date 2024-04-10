package byteback.analysis.local.vimp.tag.body;

import byteback.analysis.common.tag.TagFlagger;
import byteback.common.function.Lazy;
import soot.Body;

/**
 * @author paganma
 */
public class ExceptionalFlagger extends TagFlagger<Body, ExceptionalTag> {

    private static final Lazy<ExceptionalFlagger> instance =
            Lazy.from(()  -> new ExceptionalFlagger(ExceptionalTag.v()));

    public static ExceptionalFlagger v() {
        return instance.get();
    }

    private ExceptionalFlagger(final ExceptionalTag tag) {
        super(tag);
    }

}
