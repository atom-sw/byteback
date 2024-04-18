package byteback.syntax.scene.type.declaration.member.method.body.encoder.to_bpl;

import byteback.common.function.Lazy;
import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.body.encoder.BodyEncoder;
import soot.Body;

public class ProceduralBodyToBplEncoder implements BodyEncoder {

    private static final Lazy<ProceduralBodyToBplEncoder> INSTANCE = Lazy.from(ProceduralBodyToBplEncoder::new);

    public static ProceduralBodyToBplEncoder v() {
        return INSTANCE.get();
    }

    private ProceduralBodyToBplEncoder() {
    }

    @Override
    public void encodeBody(final Printer printer, final Body body) {
    }

}
