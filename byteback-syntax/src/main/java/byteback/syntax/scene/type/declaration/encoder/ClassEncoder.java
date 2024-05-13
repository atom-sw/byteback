package byteback.syntax.scene.type.declaration.encoder;

import byteback.syntax.encoder.Encoder;
import byteback.syntax.printer.Printer;
import soot.SootClass;

public abstract class ClassEncoder extends Encoder {

	public ClassEncoder(final Printer printer) {
		super(printer);
	}

	public abstract void encodeClass(SootClass sootClass);

}
