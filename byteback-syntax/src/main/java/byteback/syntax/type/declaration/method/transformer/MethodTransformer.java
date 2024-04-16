package byteback.syntax.type.declaration.method.transformer;

import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.type.declaration.context.ClassContext;
import byteback.syntax.type.declaration.method.walker.MethodWalker;
import byteback.syntax.type.declaration.method.context.MethodContext;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

import java.util.ArrayList;
import java.util.List;

public abstract class MethodTransformer extends MethodWalker<SceneContext, ClassContext, MethodContext> {

    @Override
    public SceneContext makeSceneContext(final Scene scene) {
        return new SceneContext(scene);
    }

    @Override
    public ClassContext makeClassContext(final SceneContext sceneContext, final SootClass sootClass) {
        return new ClassContext(sceneContext, sootClass);
    }

    @Override
    public MethodContext makeMethodContext(final ClassContext classContext, final SootMethod sootMethod) {
        return new MethodContext(classContext, sootMethod);
    }

}
