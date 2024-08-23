package byteback.syntax.scene.transformer;

import soot.Scene;

public abstract class SceneTransformer {

    /**
     * Transforms the Scene.
     *
     * @param scene The scene to be transformed.
     */
    public abstract void transformScene(final Scene scene);

}
