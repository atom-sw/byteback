package byteback.syntax.scene.type.declaration.transformer;

import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.scene.transformer.context.SceneTransformerContext;
import byteback.syntax.scene.type.declaration.context.ClassContext;
import byteback.syntax.scene.type.declaration.transformer.context.ClassTransformerContext;
import byteback.syntax.scene.type.declaration.walker.ClassWalker;
import soot.Scene;
import soot.SootClass;

public abstract class ClassTransformer extends ClassWalker<SceneTransformerContext, ClassTransformerContext> {

    public abstract void transformClass(final ClassTransformerContext classContext);

    @Override
    public SceneTransformerContext makeSceneContext(final Scene scene) {
        return new SceneTransformerContext(scene);
    }

    @Override
    public ClassTransformerContext makeClassContext(final SceneTransformerContext sceneContext,
                                                    final SootClass sootClass) {
        return new ClassTransformerContext(sceneContext, sootClass);
    }

    @Override
    public void walkClass(final ClassTransformerContext classContext) {
        transformClass(classContext);
    }

}
