package byteback.syntax.scene.context;

import byteback.syntax.context.Context;
import soot.Scene;

/**
 * A context surrounding a single scene.
 *
 * @author paganma
 */
public class SceneContext implements Context {

    private final Scene scene;

    public SceneContext(final Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }

}
