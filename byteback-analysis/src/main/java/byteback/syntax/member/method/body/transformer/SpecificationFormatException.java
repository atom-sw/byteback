package byteback.syntax.member.method.body.transformer;

import byteback.syntax.tag.LocationTag;
import byteback.syntax.transformer.TransformationException;
import soot.tagkit.Host;

/**
 * Exception signaling a validation error in a Behavior function.
 *
 * @author paganma
 */
public class SpecificationFormatException extends TransformationException {

    public SpecificationFormatException(final String message, final LocationTag locationTag) {
        super(message, locationTag);
    }

    public SpecificationFormatException(final String message, final Host host) {
        super(message, host);
    }

}
