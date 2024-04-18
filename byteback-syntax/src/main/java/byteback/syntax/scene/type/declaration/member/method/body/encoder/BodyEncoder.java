package byteback.syntax.scene.type.declaration.member.method.body.encoder;

import byteback.syntax.encoder.Encoder;
import byteback.syntax.printer.Printer;
import soot.Body;

public interface BodyEncoder extends Encoder {

    void encodeBody(final Printer printer, final Body body);

}
