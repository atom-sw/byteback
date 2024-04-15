package byteback.syntax.scene.walker;

import byteback.syntax.scene.context.SceneContext;
import soot.Scene;

import java.util.Map;

/**
 * Defines how to traverse a scene context.
 *
 * @param <T> The type of the scene context.
 * @author paganma
 */
public abstract class SceneWalker<T extends SceneContext> extends soot.SceneTransformer {

    /**
     * Builder for the Scene context.
     *
     * @param scene The scene used to build the context.
     * @return The new Scene context.
     */
    protected abstract T makeSceneContext(final Scene scene);

    /**
     * Traverses the Scene through the context.
     *
     * @param sceneContext The context built using `makeSceneContext`;
     */
    public abstract void walkScene(final T sceneContext);

    /**
     * Defined for compatibility with Soot's whole-program transformation packs.
     *
     * @param phaseName The name of the phase.
     * @param options The options passed to the phase.
     */
    @Override
    protected void internalTransform(final String phaseName, final Map<String, String> options) {
        final Scene scene = Scene.v();
        final T sceneContext = makeSceneContext(scene);
        walkScene(sceneContext);
    }

}