package byteback.syntax.scene.type.declaration.member.method.body.unit.encoder;

import byteback.syntax.encoder.Encoder;
import byteback.syntax.printer.Printer;
import soot.Unit;

public abstract class UnitEncoder extends Encoder {

    public UnitEncoder(final Printer printer) {
        super(printer);
    }

    public abstract void encodeUnit(Unit unit);

}
