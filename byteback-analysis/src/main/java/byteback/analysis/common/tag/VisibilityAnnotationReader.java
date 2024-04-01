package byteback.analysis.common.tag;

import byteback.common.function.Lazy;
import soot.tagkit.Host;
import soot.tagkit.VisibilityAnnotationTag;

/**
 * Reads the VisibilityAnnotationTag from a given host.
 */
public class VisibilityAnnotationReader extends TagReader<Host, VisibilityAnnotationTag> {

    private static final Lazy<VisibilityAnnotationReader> instance =
            Lazy.from(()  -> new VisibilityAnnotationReader("VisibilityAnnotationTag"));

    public static VisibilityAnnotationReader v() {
        return instance.get();
    }

    private VisibilityAnnotationReader(final String tagName) {
        super(tagName);
    }

}
