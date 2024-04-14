package byteback.syntax.type.declaration.transformer;

import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.type.declaration.context.ClassContext;
import byteback.syntax.type.declaration.walker.ClassWalker;
import soot.Scene;
import soot.SootClass;

public abstract class ClassTransformer extends ClassWalker<SceneContext, ClassContext> {

    public abstract void transformClass(final ClassContext classContext);

    @Override
    public SceneContext makeSceneContext(final Scene scene) {
        return new SceneContext(scene);
    }

    @Override
    public ClassContext makeClassContext(final SceneContext sceneContext, final SootClass sootClass) {
        return new ClassContext(sceneContext, sootClass);
    }

    @Override
    public void walkClass(final ClassContext classContext) {
        transformClass(classContext);
    }

}
