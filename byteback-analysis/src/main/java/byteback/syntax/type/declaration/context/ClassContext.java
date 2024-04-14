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

    public ClassContext(final Scene scene, final SootClass sootClass) {
        super(scene);
        this.sootClass = sootClass;
    }

    public ClassContext(final SceneContext sceneContext, final SootClass sootClass) {
        this(sceneContext.getScene(), sootClass);
    }

    public SootClass getSootClass() {
        return sootClass;
    }

}
