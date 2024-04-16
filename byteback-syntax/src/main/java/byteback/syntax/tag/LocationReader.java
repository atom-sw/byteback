package byteback.syntax.tag;

import byteback.common.function.Lazy;
import soot.tagkit.Host;

/**
 * Reads a LineNumberTag from a host.
 *
 * @author paganma
 */
public class LocationReader extends TagReader<Host, LocationTag> {

    private static final Lazy<LocationReader> INSTANCE = Lazy.from(() ->
            new LocationReader(LocationTag.NAME));

    public static LocationReader v() {
        return INSTANCE.get();
    }

    /**
     * Constructs a new location reader
     *
     * @param tagName The name associated with the location tag.
     */
    private LocationReader(final String tagName) {
        super(tagName);
    }

}
