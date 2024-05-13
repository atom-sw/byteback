package byteback.syntax.scene.type.declaration.member.method.body.encoder;

import byteback.syntax.encoder.Encoder;
import byteback.syntax.printer.Printer;
import soot.Body;

public abstract class BodyEncoder extends Encoder {

	public BodyEncoder(final Printer printer) {
		super(printer);
	}

	public abstract void encodeBody(final Body body);

}
