package byteback.syntax.transformer;

import byteback.syntax.tag.LocationTag;

public class PositionalMessage extends StatusMessage {

    private final LocationTag locationTag;

    public PositionalMessage(final Type type, final String message, final LocationTag locationTag) {
        super(type, message);
        this.locationTag = locationTag;
    }

    public LocationTag getLocationTag() {
        return locationTag;
    }

}
