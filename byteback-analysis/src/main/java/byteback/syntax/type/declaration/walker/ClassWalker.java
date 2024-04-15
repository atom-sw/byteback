package byteback.syntax.type.declaration.walker;

import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.scene.walker.SceneWalker;
import byteback.syntax.type.declaration.context.ClassContext;
import soot.Scene;
import soot.SootClass;

/**
 * Defines how to transform a class.
 *
 * @param <S> The type of the outer scene context.
 * @param <C> The type of the class context.
 * @author paganma
 */
public abstract class ClassWalker<S extends SceneContext, C extends ClassContext>
        extends SceneWalker<S> {

    public abstract C makeClassContext(final SceneContext sceneContext, final SootClass sootClass);

    public abstract void walkClass(final C classContext);

    @Override
    public void walkScene(final SceneContext sceneContext) {
        final Scene scene = sceneContext.getScene();

        for (final SootClass sootClass : scene.getClasses()) {
            final C classContext = makeClassContext(sceneContext, sootClass);
            walkClass(classContext);
        }
    }

}
