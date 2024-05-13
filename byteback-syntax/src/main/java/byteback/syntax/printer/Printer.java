package byteback.syntax.printer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Prints a syntax tree to a {@link PrintWriter} instance.
 *
 * @author paganma
 */
public class Printer implements AutoCloseable {

	private static class Separator {

		public final String string;

		public volatile boolean isFirst;

		public Separator(final String string) {
			this.string = string;
			this.isFirst = true;
		}

	}

	private final PrintWriter writer;

	private final Deque<Separator> separators;

	public Printer(final PrintWriter writer) {
		this.writer = writer;
		this.separators = new ArrayDeque<>();
	}

	public Printer(final String fileName) throws FileNotFoundException {
		this(new PrintWriter(fileName));
	}

	public void print(final String string) {
		writer.write(string);
	}

	public void printLine(final String string) {
		writer.println(string);
	}

	public void endLine() {
		writer.println();
	}

	public void startItems(final String separatorString) {
		final var separator = new Separator(separatorString);
		separators.push(separator);
	}

	public void separate() {
		final Separator topSeparator = separators.peek();
		assert topSeparator != null;

		if (topSeparator.isFirst) {
			topSeparator.isFirst = false;
			return;
		}

		print(topSeparator.string);
	}

	public void endItems() {
		separators.pop();
	}

	public void close() {
		writer.flush();
		writer.close();
	}

}
