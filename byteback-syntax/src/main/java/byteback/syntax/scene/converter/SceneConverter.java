package byteback.syntax.scene.converter;

import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.scene.walker.SceneWalker;
import soot.Scene;

public abstract class SceneConverter extends SceneWalker<SceneContext> {

    @Override
    protected SceneContext makeSceneContext(Scene scene) {
        return null;
    }

}
