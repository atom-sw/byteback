package byteback.syntax.scene.transformer;

import soot.Scene;

import java.util.Map;

public abstract class SceneTransformer extends soot.SceneTransformer {

    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {
        transformScene(Scene.v());
    }

    public abstract void transformScene(final Scene scene);

}
