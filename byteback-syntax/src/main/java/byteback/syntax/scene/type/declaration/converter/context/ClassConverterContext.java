package byteback.syntax.scene.type.declaration.converter.context;

import byteback.syntax.scene.converter.context.SceneConverterContext;
import byteback.syntax.scene.type.declaration.context.ClassContext;
import soot.SootClass;

public class ClassConverterContext extends ClassContext<SceneConverterContext> {

    /**
     * Constructs a new ClassContext.
     *
     * @param sceneContext The outer scene context.
     * @param sootClass    The class of this context.
     */
    public ClassConverterContext(final SceneConverterContext sceneContext, final SootClass sootClass) {
        super(sceneContext, sootClass);
    }

}
