package byteback.encoder;

import byteback.analysis.Scheduler;
import java.io.PrintWriter;
import java.util.Stack;
import sootup.core.views.View;

public interface Encoder<T extends Context> {

	public T getContext();

	public Stack<Separator> getSeparatorStack();

	public default View<?> getView() {
		return getContext().getView();
	}

	public default Scheduler getScheduler() {
		return getContext().getScheduler();
	}

	public default PrintWriter getWriter() {
		return getContext().getWriter();
	}

	public default void encode(final String string) {
		getWriter().write(string);
	}

	public default void encodeNewline() {
		encode("\n");
	}

	public default void encodeTab() {
		encode("\t");
	}

	public default void encodeSpace() {
		encode(" ");
	}

	default void startSequence(final String separatorString) {
		getSeparatorStack().push(new Separator(separatorString));
	}

	default void endSequence() {
		final Stack<Separator> separatorStack = getSeparatorStack();
		if (!separatorStack.isEmpty()) {
			separatorStack.pop();
		}
	}

	default void startItem() {
		final Stack<Separator> separatorStack = getSeparatorStack();
		if (!separatorStack.isEmpty()) {
			final Separator currentSeparator = separatorStack.peek();
			if (!currentSeparator.isSeparating()) {
				currentSeparator.setSeparating();
			} else {
				encode(currentSeparator.toString());
			}
		}
	}

}
