package byteback.syntax.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Tag representing a location in a file, by storing the path to the file and line number.
 *
 * @author paganma
 */
public class LocationTag implements Tag {

    public static final int DEFAULT_LINE = -1;

    public static final String DEFAULT_PATH = "unknown";

    private static final Lazy<LocationTag> DEFAULT_INSTANCE =
            Lazy.from(() -> new LocationTag(DEFAULT_PATH, DEFAULT_LINE));

    public static final String NAME = "LocationTag";

    private final int line;

    private final String path;

    /**
     * Constructs a new LocationTag.
     *
     * @param path The path to the file corresponding to this location.
     * @param line The line number corresponding to the location.
     */
    LocationTag(final String path, final int line) {
        this.path = path;
        this.line = line;
    }

    /**
     * Getter for the line contained in the location tag.
     *
     * @return The line contained in the location tag.
     */
    public int getLine() {
        return line;
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Getter for the path contained in the location tag.
     *
     * @return The path contained in the location tag.
     */
    public String getPath() {
        return path;
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        return new byte[0];
    }

    /**
     * Getter for the default instance of LocationTag. This can be used to represent an unknown location.
     *
     * @return The default instance of LocationTag.
     */
    public static LocationTag defaultV() {
        return DEFAULT_INSTANCE.get();
    }

    @Override
    public String toString() {
        return path + " (line: " + line + ")";
    }

}
