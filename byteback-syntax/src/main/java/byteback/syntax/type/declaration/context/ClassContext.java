package byteback.syntax.type.declaration.context;

import byteback.syntax.scene.context.SceneContext;
import soot.Scene;
import soot.SootClass;

/**
 * A context surrounding a single class.
 *
 * @author paganma
 */
public class ClassContext extends SceneContext {

    final SootClass sootClass;

    /**
     * Constructs a new ClassContext.
     *
     * @param scene     The scene enclosing this context.
     * @param sootClass The class of this context.
     */
    public ClassContext(final Scene scene, final SootClass sootClass) {
        super(scene);
        this.sootClass = sootClass;
    }

    /**
     * Constructs a new ClassContext.
     *
     * @param sceneContext The outer scene context.
     * @param sootClass    The class of this context.
     */
    public ClassContext(final SceneContext sceneContext, final SootClass sootClass) {
        this(sceneContext.getScene(), sootClass);
    }

    /**
     * Getter for the Soot Class in this context.
     *
     * @return The Soot Class in this context.
     */
    public SootClass getSootClass() {
        return sootClass;
    }

}
