package byteback.syntax.scene.transformer;

import byteback.syntax.transformer.TransformerContext;
import soot.Scene;

public class SceneContext extends TransformerContext {

    private final Scene scene;

    public SceneContext(final Scene scene) {
        this.scene = scene;
    }

}
