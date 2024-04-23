package byteback.syntax.scene.encoder;

import byteback.syntax.encoder.Encoder;
import byteback.syntax.printer.Printer;
import soot.Scene;

public abstract class SceneEncoder extends Encoder {

    public SceneEncoder(final Printer printer) {
        super(printer);
    }

    public abstract void encodeScene(Scene scene);

}
