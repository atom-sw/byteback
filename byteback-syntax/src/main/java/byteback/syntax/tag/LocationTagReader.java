package byteback.syntax.tag;

import byteback.common.function.Lazy;
import soot.tagkit.Host;

/**
 * Reads a LineNumberTag from a host.
 *
 * @author paganma
 */
public class LocationTagReader extends TagReader<Host, LocationTag> {

    private static final Lazy<LocationTagReader> INSTANCE =
            Lazy.from(() -> new LocationTagReader(LocationTag.NAME));

    /**
     * Constructs a new location reader
     *
     * @param tagName The name associated with the location tag.
     */
    private LocationTagReader(final String tagName) {
        super(tagName);
    }

    public static LocationTagReader v() {
        return INSTANCE.get();
    }

}
