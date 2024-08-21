package byteback.syntax.scene.type.declaration.member.method.encoder;

import byteback.syntax.encoder.Encoder;
import byteback.syntax.printer.Printer;
import soot.SootMethod;

public abstract class MethodEncoder extends Encoder {

	public MethodEncoder(final Printer printer) {
		super(printer);
	}

	public abstract void encodeMethod(SootMethod sootMethod);

}
