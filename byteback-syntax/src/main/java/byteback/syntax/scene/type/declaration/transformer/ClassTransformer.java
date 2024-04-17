package byteback.syntax.scene.type.declaration.transformer;

import byteback.syntax.scene.transformer.SceneTransformer;
import byteback.syntax.scene.transformer.context.SceneTransformationContext;
import byteback.syntax.scene.type.declaration.transformer.context.ClassTransformationContext;
import soot.Scene;
import soot.SootClass;

/**
 * Defines how to transform a class.
 *
 * @param <S> The type of the outer scene context.
 * @param <C> The type of the class context.
 * @author paganma
 */
public abstract class ClassTransformer extends SceneTransformer {

    public abstract void transformClass(final ClassTransformationContext classContext);

    public void transformScene(final SceneTransformationContext sceneTransformationContext) {
        final Scene scene = sceneTransformationContext.getScene();

        for (final SootClass sootClass : scene.getClasses()) {
            final var classContext = new ClassTransformationContext(sceneTransformationContext, sootClass);
            transformClass(classContext);
        }
    }

}
