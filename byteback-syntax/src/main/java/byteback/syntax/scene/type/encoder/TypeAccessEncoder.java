package byteback.syntax.scene.type.encoder;

import byteback.syntax.printer.Printer;
import soot.Scene;
import soot.Type;

public interface TypeAccessEncoder {

    void encodeTypeAccess(final Printer printer, final Type type);

}
