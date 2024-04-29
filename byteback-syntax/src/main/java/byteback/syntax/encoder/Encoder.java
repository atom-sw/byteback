package byteback.syntax.encoder;

import byteback.syntax.printer.Printer;

/**
 * Base class for a syntax Encoder.
 *
 * @author paganma
 */
public abstract class Encoder {

    /**
     * The printer used by the encoder.
     */
    protected final Printer printer;

    /**
     * Constructs a new Encoder.
     *
     * @param printer The printer to which the encoder will output the syntax tree.
     */
    public Encoder(final Printer printer) {
        this.printer = printer;
    }

}
