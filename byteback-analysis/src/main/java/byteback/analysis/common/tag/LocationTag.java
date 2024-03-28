package byteback.analysis.common.tag;

import byteback.analysis.common.Hosts;
import soot.tagkit.*;

import java.util.Optional;

/**
 * Represents a location in a file.
 *
 * @author paganma
 */
public class LocationTag implements Tag {

    public static final String NAME = "LocationTag";

    /**
     * Reconstructs a position from multiple hosts. If more than one host has position information, it takes the
     * information from the latter.
     * @param hosts The hosts from which to extract the position information.
     * @return The corresponding LocationTag.
     */
    public static LocationTag fromHosts(final Host... hosts) {
        String path = null;
        int line = -1;

        for (final Host host : hosts) {
            if (path == null) {
                final Optional<SourceFileTag> sourceTagOptional  =
                    Hosts.v().getTag(host, "SourceFileTag")
                            .map(SourceFileTag.class::cast);

                if (sourceTagOptional.isPresent()) {
                    path = sourceTagOptional.get().getAbsolutePath();
                }
            }

            final Optional<LineNumberTag> lineNumberTagOptional =
                    Hosts.v().getTag(host, "LineNumberTag")
                            .map(LineNumberTag.class::cast);

            if (lineNumberTagOptional.isPresent()) {
                final int newLine = lineNumberTagOptional.get().getLineNumber();

                if (newLine > -1) {
                    line = newLine;
                }
            }
        }

        return new LocationTag(path, line);
    }

    private final String path;

    private final int line;

    public LocationTag(final String path, final int line) {
        this.path = path;
        this.line = line;
    }

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
