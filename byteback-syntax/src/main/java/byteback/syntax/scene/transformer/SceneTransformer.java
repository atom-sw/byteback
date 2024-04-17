package byteback.syntax.scene.transformer;

import byteback.syntax.scene.context.SceneContext;
import soot.Scene;

import java.util.Map;

public abstract class SceneTransformer extends soot.SceneTransformer {

    /**
     * Traverses the Scene through the context.
     *
     * @param sceneTransformationContext The context built using `makeSceneContext`;
     */
    public abstract void transformScene(final SceneContext sceneTransformationContext);

    /**
     * Defined for compatibility with Soot's whole-program transformation packs.
     *
     * @param phaseName The name of the phase.
     * @param options The options passed to the phase.
     */
    @Override
    protected void internalTransform(final String phaseName, final Map<String, String> options) {
        final Scene scene = Scene.v();
        final var sceneContext = new SceneContext(scene);
        transformScene(sceneContext);
    }

}