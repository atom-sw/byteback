package byteback.syntax.encoder;

import byteback.syntax.printer.Printer;

public abstract class Encoder {

	protected Printer printer;

	public Encoder(final Printer printer) {
		this.printer = printer;
	}
	
}
