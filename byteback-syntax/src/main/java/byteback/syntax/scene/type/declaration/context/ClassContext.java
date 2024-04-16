package byteback.syntax.scene.type.declaration.context;

import byteback.syntax.context.Context;
import byteback.syntax.scene.context.SceneContext;
import soot.Scene;
import soot.SootClass;

/**
 * A context surrounding a single class.
 *
 * @author paganma
 */
public abstract class ClassContext<S extends SceneContext> implements Context {

    final S sceneContext;

    final SootClass sootClass;

    /**
     * Constructs a new ClassContext.
     *
     * @param sceneContext The outer scene context.
     * @param sootClass    The class of this context.
     */
    public ClassContext(final S sceneContext, final SootClass sootClass) {
        this.sceneContext = sceneContext;
        this.sootClass = sootClass;
    }

    /**
     * Getter for the Soot Class in this context.
     *
     * @return The Soot Class in this context.
     */
    public SootClass getSootClass() {
        return sootClass;
    }

    public S getSceneContext() {
        return sceneContext;
    }

}
