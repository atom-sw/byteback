package byteback.syntax.scene.transformer;

import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.scene.walker.SceneWalker;
import soot.Scene;

/**
 * Transformer for a Soot Scene.
 *
 * @author paganma
 */
public abstract class SceneTransformer extends SceneWalker<SceneContext> {

    @Override
    public SceneContext makeSceneContext(final Scene scene) {
        return new SceneContext(scene);
    }

}
