package byteback.syntax.tag;

import byteback.common.function.Lazy;
import soot.tagkit.Host;
import soot.tagkit.LineNumberTag;

/**
 * Reads a LineNumberTag from a host.
 *
 * @author paganma
 */
public class LineNumberTagReader extends TagReader<Host, LineNumberTag> {

    private static final Lazy<LineNumberTagReader> INSTANCE = Lazy.from(
            () -> new LineNumberTagReader(LineNumberTag.NAME));

    /**
     * Constructs a new line number reader.
     *
     * @param tagName The name associated with the line number tag.
     */
    private LineNumberTagReader(final String tagName) {
        super(tagName);
    }

    public static LineNumberTagReader v() {
        return INSTANCE.get();
    }

}
