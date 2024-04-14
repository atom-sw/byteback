package byteback.syntax.tag;

import byteback.common.function.Lazy;
import soot.tagkit.Host;
import soot.tagkit.LineNumberTag;

/**
 * Reads a LineNumberTag from a host.
 *
 * @author paganma
 */
public class LineNumberReader extends TagReader<Host, LineNumberTag> {

    private static final Lazy<LineNumberReader> INSTANCE = Lazy.from(() ->
            new LineNumberReader(LineNumberTag.NAME));

    public static LineNumberReader v() {
        return INSTANCE.get();
    }

    private LineNumberReader(final String tagName) {
        super(tagName);
    }

}
