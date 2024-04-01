package byteback.analysis.common.transformer;

import byteback.analysis.common.tag.LocationTag;

/**
 * Exception signaling a problem during a transformation.
 *
 * @author paganma
 */
public class TransformationException extends RuntimeException {

    public TransformationException(final String message, final LocationTag locationTag) {
        super(locationTag + " : " + message);
    }

}
