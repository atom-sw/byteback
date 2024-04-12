package byteback.converter.common.scene;

import byteback.syntax.scene.transformer.SceneTransformer;

import java.io.PrintWriter;

public abstract class SceneEncoder extends SceneTransformer {

    protected final PrintWriter writer;

    public SceneEncoder(final PrintWriter writer) {
        this.writer = writer;
    }

}
