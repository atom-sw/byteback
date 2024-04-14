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

    public static final String DEFAULT_PATH = "unknown";

    public static final int DEFAULT_LINE = -1;

    private static final Lazy<LocationTag> DEFAULT_INSTANCE =
            Lazy.from(() -> new LocationTag(DEFAULT_PATH, DEFAULT_LINE));

    public static LocationTag defaultV() {
        return DEFAULT_INSTANCE.get();
    }

    private final String path;

    private final int line;

    LocationTag(final String path, final int line) {
        this.path = path;
        this.line = line;
    }

    public static final String NAME = "LocationTag";

    public String getPath() {
        return path;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        return new byte[0];
    }

    @Override
    public String toString() {
        return path + " (line: " + line + ")";
    }

}
