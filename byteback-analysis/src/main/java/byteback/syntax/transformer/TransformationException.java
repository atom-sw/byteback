package byteback.syntax.transformer;

import byteback.syntax.tag.LocationReader;
import byteback.syntax.tag.LocationTag;
import soot.tagkit.Host;

/**
 * Exception signaling a problem during a transformation.
 *
 * @author paganma
 */
public class TransformationException extends RuntimeException {

    public TransformationException(final String message, final LocationTag locationTag) {
        super(locationTag + " : " + message);
    }

    public TransformationException(final String message, final Host host) {
        this(message, LocationReader.v().get(host).orElse(LocationTag.defaultV()));
    }

    public TransformationException(final String message) {
        super(message);
    }

}
