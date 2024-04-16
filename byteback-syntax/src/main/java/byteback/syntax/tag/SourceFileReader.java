package byteback.syntax.tag;

import byteback.common.function.Lazy;
import soot.tagkit.Host;
import soot.tagkit.SourceFileTag;

/**
 * Reads a SourceFileTag from a host.
 *
 * @author paganma
 */
public class SourceFileReader extends TagReader<Host, SourceFileTag> {

    private static final Lazy<SourceFileReader> INSTANCE =
            Lazy.from(() -> new SourceFileReader(SourceFileTag.NAME));

    public static SourceFileReader v() {
        return INSTANCE.get();
    }

    /**
     * Constructs a new SourceFileReader.
     *
     * @param tagName The name associated with the SourceFileTag.
     */
    private SourceFileReader(final String tagName) {
        super(tagName);
    }

}
