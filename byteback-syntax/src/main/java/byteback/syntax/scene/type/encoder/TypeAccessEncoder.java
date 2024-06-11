package byteback.syntax.scene.type.encoder;

import byteback.syntax.encoder.Encoder;
import byteback.syntax.printer.Printer;
import soot.Type;

public abstract class TypeAccessEncoder extends Encoder {

	public TypeAccessEncoder(final Printer printer) {
		super(printer);
	}

	public abstract void encodeTypeAccess(final Type type);

}
