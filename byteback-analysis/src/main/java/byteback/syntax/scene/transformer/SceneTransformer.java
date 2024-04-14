package byteback.syntax.scene.transformer;

import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.scene.walker.SceneWalker;
import soot.Scene;

/**
 * Transforms a Soot scene.
 *
 * @author paganma
 */
public abstract class SceneTransformer extends SceneWalker<SceneContext> {

    public abstract void transformScene(final SceneContext sceneContext);

    @Override
    public SceneContext makeSceneContext(final Scene scene) {
        return new SceneContext(scene);
    }

    @Override
    public void walkScene(final SceneContext sceneContext) {
        transformScene(sceneContext);
    }

}
