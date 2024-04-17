package byteback.syntax.printer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Deque;

public class Printer {

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

    public void endLine() {
        writer.println();
    }

    public void startItems(final String string) {
        final var separator = new Separator(string);
        separators.add(separator);
    }

    public void printItem(final String string) {
        final Separator topSeparator = separators.pop();

        if (topSeparator.isFirst) {
            writer.write(topSeparator.string);
        }

        print(string);
    }

    public void endItems() {
        separators.pop();
    }

}
