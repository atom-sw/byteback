package byteback.syntax.scene.transformer.context;

import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.transformer.context.TransformerContext;
import soot.Scene;

public class SceneTransformerContext extends SceneContext implements TransformerContext {

    /**
     * Constructs a new {@link SceneContext}.
     *
     * @param scene The scene corresponding to this context.
     */
    public SceneTransformerContext(final Scene scene) {
        super(scene);
    }

}
