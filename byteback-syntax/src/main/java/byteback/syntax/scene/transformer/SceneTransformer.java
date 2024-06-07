package byteback.syntax.scene.transformer;

import byteback.syntax.transformer.Transformer;
import soot.Scene;

import java.util.Map;

public abstract class SceneTransformer extends soot.SceneTransformer implements Transformer {

    /**
     * Transforms the Scene.
     *
     * @param scene The scene to be transformed.
     */
    public abstract void transformScene(final Scene scene);

    /**
     * Defined for compatibility with Soot's whole-program transformation packs.
     *
     * @param phaseName The name of the phase.
     * @param options   The options passed to the phase.
     */
    @Override
    protected void internalTransform(final String phaseName, final Map<String, String> options) {
        final Scene scene = Scene.v();
        transformScene(scene);
    }

}