package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.common.tag.LocationTag;
import byteback.analysis.common.transformer.TransformationException;

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