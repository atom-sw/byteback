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

    /**
     * Constructs a new TransformationException associated to a location.
     *
     * @param message     The message of the exception.
     * @param locationTag The location at which the exception occurred.
     */
    public TransformationException(final String message, final LocationTag locationTag) {
        super(locationTag + " : " + message);
    }

    /**
     * Constructs a new TransformationException associated to a host's location.
     *
     * @param message The message of the exception.
     * @param host    The host containing the location tag corresponding to this exception.
     */
    public TransformationException(final String message, final Host host) {
        this(message, LocationReader.v().get(host).orElse(LocationTag.defaultV()));
    }

    /**
     * Constructs a simple TransformationException
     *
     * @param message The message of the exception.
     */
    public TransformationException(final String message) {
        super(message);
    }

}
