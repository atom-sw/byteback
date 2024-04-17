package byteback.syntax.scene.type.declaration.transformer.context;

import byteback.syntax.scene.transformer.context.SceneTransformationContext;
import byteback.syntax.transformer.context.TransformationContext;
import byteback.syntax.scene.type.declaration.context.ClassContext;
import soot.SootClass;

public class ClassTransformationContext extends ClassContext<SceneTransformationContext> implements TransformationContext {

    /**
     * Constructs a new ClassContext.
     *
     * @param sceneContext The outer scene context.
     * @param sootClass    The class of this context.
     */
    public ClassTransformationContext(final SceneTransformationContext sceneContext, final SootClass sootClass) {
        super(sceneContext, sootClass);
    }

}
