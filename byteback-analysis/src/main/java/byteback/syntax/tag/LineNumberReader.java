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

    /**
     * Constructs a new line number reader.
     *
     * @param tagName The name associated with the line number tag.
     */
    private LineNumberReader(final String tagName) {
        super(tagName);
    }

}
