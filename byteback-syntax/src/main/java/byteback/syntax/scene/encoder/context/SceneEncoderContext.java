package byteback.syntax.scene.encoder.context;

import byteback.syntax.encoder.context.EncoderContext;
import byteback.syntax.scene.context.SceneContext;
import soot.Scene;

import java.io.PrintWriter;

public class SceneEncoderContext extends SceneContext implements EncoderContext {

    private final PrintWriter writer;

    public SceneEncoderContext(final Scene scene, final PrintWriter writer) {
        super(scene);
        this.writer = writer;
    }

    public PrintWriter getWriter() {
        return writer;
    }

}
