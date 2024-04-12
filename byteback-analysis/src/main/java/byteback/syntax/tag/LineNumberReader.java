package byteback.syntax.tag;

import byteback.common.function.Lazy;
import soot.tagkit.Host;
import soot.tagkit.LineNumberTag;

/**
 * Reads the LineNumberTag from a given host.
 *
 * @author paganma
 */
public class LineNumberReader extends TagReader<Host, LineNumberTag> {

    private static final Lazy<LineNumberReader> instance = Lazy.from(()  ->
            new LineNumberReader("LineNumberTag"));

    public static LineNumberReader v() {
        return instance.get();
    }

    private LineNumberReader(final String tagName) {
        super(tagName);
    }

}
