package byteback.syntax.scene.type.declaration.transformer.context;

import byteback.syntax.scene.transformer.context.SceneTransformerContext;
import byteback.syntax.transformer.context.TransformerContext;
import byteback.syntax.scene.type.declaration.context.ClassContext;
import soot.SootClass;

public class ClassTransformerContext extends ClassContext<SceneTransformerContext> implements TransformerContext {

    /**
     * Constructs a new ClassContext.
     *
     * @param sceneContext The outer scene context.
     * @param sootClass    The class of this context.
     */
    public ClassTransformerContext(final SceneTransformerContext sceneContext, final SootClass sootClass) {
        super(sceneContext, sootClass);
    }

}
