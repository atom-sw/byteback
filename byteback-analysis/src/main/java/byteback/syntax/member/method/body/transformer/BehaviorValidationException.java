package byteback.syntax.member.method.body.transformer;

import byteback.syntax.tag.LocationTag;
import byteback.syntax.transformer.TransformationException;

/**
 * Exception signaling a validation error in a Behavior function.
 *
 * @author paganma
 */
public class BehaviorValidationException extends TransformationException {

    public BehaviorValidationException(final String message, final LocationTag locationTag) {
        super(message, locationTag);
    }

}
