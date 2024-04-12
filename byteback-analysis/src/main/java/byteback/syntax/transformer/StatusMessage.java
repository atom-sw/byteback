package byteback.syntax.transformer;

public abstract class StatusMessage {

    public enum Type {
        INFO, ERROR, WARNING
    }

    private final Type type;

    private final String message;

    public StatusMessage(final Type type, final String message) {
        this.type = type;
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

}
