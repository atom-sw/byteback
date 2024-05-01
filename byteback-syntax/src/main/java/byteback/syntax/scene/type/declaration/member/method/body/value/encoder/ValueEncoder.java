package byteback.syntax.scene.type.declaration.member.method.body.value.encoder;

import byteback.syntax.encoder.Encoder;
import byteback.syntax.printer.Printer;
import soot.Value;

public abstract class ValueEncoder extends Encoder {

    public ValueEncoder(final Printer printer) {
        super(printer);
    }

    public abstract void encodeValue(Value value);

}
