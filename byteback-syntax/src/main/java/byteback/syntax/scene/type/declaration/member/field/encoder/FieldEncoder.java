package byteback.syntax.scene.type.declaration.member.field.encoder;

import byteback.syntax.encoder.Encoder;
import byteback.syntax.printer.Printer;
import soot.SootField;

public abstract class FieldEncoder extends Encoder {

    public FieldEncoder(final Printer printer) {
        super(printer);
    }

    public abstract void encodeField(final SootField field);

}
