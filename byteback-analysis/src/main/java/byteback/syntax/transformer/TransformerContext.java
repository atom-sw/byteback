package byteback.syntax.transformer;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class TransformerContext {

    private final Deque<StatusMessage> messages;

    public TransformerContext() {
        this.messages = new ArrayDeque<>();
    }

    public TransformerContext(final Deque<StatusMessage> messages) {
        this.messages = messages;
    }

    public Deque<StatusMessage> getMessages() {
        return messages;
    }

}
