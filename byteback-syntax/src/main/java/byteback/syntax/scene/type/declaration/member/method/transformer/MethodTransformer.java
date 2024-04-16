package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.syntax.scene.transformer.context.SceneTransformerContext;
import byteback.syntax.scene.type.declaration.member.method.walker.MethodWalker;
import byteback.syntax.scene.type.declaration.member.method.transformer.context.MethodTransformerContext;

import byteback.syntax.scene.type.declaration.transformer.context.ClassTransformerContext;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

public abstract class MethodTransformer extends
        MethodWalker<SceneTransformerContext, ClassTransformerContext, MethodTransformerContext> {

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
    public MethodTransformerContext makeMethodContext(final ClassTransformerContext classContext,
                                                      final SootMethod sootMethod) {
        return new MethodTransformerContext(classContext, sootMethod);
    }

}
