package byteback.syntax.scene.type.declaration.converter.to_boogie;

import byteback.syntax.scene.converter.context.SceneConverterContext;
import byteback.syntax.scene.type.declaration.converter.context.ClassConverterContext;
import byteback.syntax.scene.type.declaration.walker.ClassWalker;
import soot.Scene;
import soot.SootClass;

public class ClassToBplConverter extends ClassWalker<SceneConverterContext, ClassConverterContext> {

    @Override
    protected SceneConverterContext makeSceneContext(final Scene scene) {
        return new SceneConverterContext(scene);
    }

    @Override
    public ClassConverterContext makeClassContext(final SceneConverterContext sceneContext,
                                                  final SootClass sootClass) {
        return new ClassConverterContext(sceneContext, sootClass);
    }

    @Override
    public void walkClass(final ClassConverterContext classContext) {

    }

}
