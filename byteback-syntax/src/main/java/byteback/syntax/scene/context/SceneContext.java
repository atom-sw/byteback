package byteback.syntax.scene.context;

import byteback.syntax.context.Context;
import soot.Scene;

/**
 * A context surrounding a single scene.
 *
 * @author paganma
 */
public class SceneContext implements Context {

    /**
     * The scene in this context
     */
    private final Scene scene;

    /**
     * Constructs a new {@link SceneContext}.
     *
     * @param scene The scene corresponding to this context.
     */
    public SceneContext(final Scene scene) {
        this.scene = scene;
    }

    /**
     * Getter for the scene in this context.
     *
     * @return The scene instance associated to this context.
     */
    public Scene getScene() {
        return scene;
    }

}
