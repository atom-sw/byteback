package byteback.syntax.scene.type.declaration.transformer;

import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.scene.transformer.SceneTransformer;
import byteback.syntax.scene.type.declaration.context.ClassContext;
import soot.Scene;
import soot.SootClass;

/**
 * Defines how to transform a class.
 *
 * @author paganma
 */
public abstract class ClassTransformer extends SceneTransformer {

    public abstract void transformClass(final ClassContext classContext);

    public void transformScene(final SceneContext sceneContext) {
        final Scene scene = sceneContext.getScene();

        for (final SootClass sootClass : scene.getClasses()) {
            final var classContext = new ClassContext(sceneContext, sootClass);
            transformClass(classContext);
        }
    }

}
