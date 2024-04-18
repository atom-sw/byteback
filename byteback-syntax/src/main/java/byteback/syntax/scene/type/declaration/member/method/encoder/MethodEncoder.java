package byteback.syntax.scene.type.declaration.member.method.encoder;

import byteback.syntax.encoder.Encoder;
import byteback.syntax.printer.Printer;
import soot.SootMethod;

public interface MethodEncoder extends Encoder {

    void encodeMethod(final Printer printer, final SootMethod sootMethod);

}
