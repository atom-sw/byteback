package byteback.syntax.scene.walker;

import byteback.syntax.scene.context.SceneContext;
import soot.Scene;

import java.util.Map;

/**
 * Defines how to transform a scene context.
 *
 * @param <T> The type of the scene context.
 * @author paganma
 */
public abstract class SceneWalker<T extends SceneContext> extends soot.SceneTransformer {

    public abstract T makeSceneContext(final Scene scene);

    public abstract void walkScene(final T sceneContext);

    @Override
    protected void internalTransform(final String phaseName, final Map<String, String> options) {
        final Scene scene = Scene.v();
        final T sceneContext = makeSceneContext(scene);
        walkScene(sceneContext);
    }

}