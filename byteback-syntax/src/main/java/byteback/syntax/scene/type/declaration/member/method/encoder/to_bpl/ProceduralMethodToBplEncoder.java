package byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl;

import byteback.common.function.Lazy;
import byteback.syntax.encoder.Encoder;
import byteback.syntax.printer.Printer;
import soot.SootMethod;

public class ProceduralMethodToBplEncoder implements Encoder {

    private static final Lazy<ProceduralMethodToBplEncoder> INSTANCE = Lazy.from(ProceduralMethodToBplEncoder::new);

    public static ProceduralMethodToBplEncoder v() {
        return INSTANCE.get();
    }

    private ProceduralMethodToBplEncoder() {
    }

    public void encodeProceduralMethod(final Printer printer, final SootMethod sootMethod) {
    }

}
