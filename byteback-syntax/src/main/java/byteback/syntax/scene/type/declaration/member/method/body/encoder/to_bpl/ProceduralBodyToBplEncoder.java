package byteback.syntax.scene.type.declaration.member.method.body.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.body.encoder.BodyEncoder;
import soot.Body;
import soot.Unit;

public class ProceduralBodyToBplEncoder extends BodyEncoder {

    public ProceduralBodyToBplEncoder(final Printer printer) {
			super(printer);
    }

    @Override
    public void encodeBody(final Body body) {
        for (final Unit unit : body.getUnits()) {
            
        }
    }

}
