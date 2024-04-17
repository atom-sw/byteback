package byteback.syntax.scene.transformer.context;

import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.transformer.context.TransformationContext;
import soot.Scene;

public class SceneTransformationContext extends SceneContext implements TransformationContext {

    public SceneTransformationContext(final Scene scene) {
        super(scene);
    }

}
