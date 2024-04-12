package byteback.syntax.type.declaration.transformer;

import byteback.syntax.scene.transformer.SceneTransformer;
import soot.Scene;
import soot.SootClass;

public abstract class ClassTransformer extends SceneTransformer {

    @Override
    public void transformScene(final Scene scene) {
        for (final SootClass sootClass : scene.getClasses()) {
            transformClass(scene, sootClass);
        }
    }

    public abstract void transformClass(final Scene scene, final SootClass sootClass);

}
