package byteback.syntax.scene.encoder;

import byteback.syntax.encoder.Encoder;
import byteback.syntax.printer.Printer;
import soot.Scene;

public interface SceneEncoder extends Encoder {

    void encodeScene(Printer printer, Scene scene);

}
