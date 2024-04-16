package byteback.converter.common.scene;

import byteback.syntax.scene.walker.SceneWalker;

import java.io.PrintWriter;

public abstract class SceneEncoder extends SceneWalker {

    protected final PrintWriter writer;

    public SceneEncoder(final PrintWriter writer) {
        this.writer = writer;
    }

}
