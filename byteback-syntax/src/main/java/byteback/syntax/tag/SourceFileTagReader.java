package byteback.syntax.tag;

import byteback.common.function.Lazy;
import soot.tagkit.Host;
import soot.tagkit.SourceFileTag;

/**
 * Reads a SourceFileTag from a host.
 *
 * @author paganma
 */
public class SourceFileTagReader extends TagReader<Host, SourceFileTag> {

    private static final Lazy<SourceFileTagReader> INSTANCE =
            Lazy.from(() -> new SourceFileTagReader(SourceFileTag.NAME));

    /**
     * Constructs a new SourceFileReader.
     *
     * @param tagName The name associated with the SourceFileTag.
     */
    private SourceFileTagReader(final String tagName) {
        super(tagName);
    }

    public static SourceFileTagReader v() {
        return INSTANCE.get();
    }

}
