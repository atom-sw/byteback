package byteback.analysis.body.vimp.transformer;

import byteback.analysis.common.tag.LocationTag;
import byteback.analysis.common.transformer.TransformationException;

public class BehaviorValidationException extends TransformationException {

    public BehaviorValidationException(final String message, final LocationTag locationTag) {
        super(message, locationTag);
    }

}
