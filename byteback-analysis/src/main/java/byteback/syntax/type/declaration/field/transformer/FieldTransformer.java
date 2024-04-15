package byteback.syntax.type.declaration.field.transformer;

import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.type.declaration.context.ClassContext;
import byteback.syntax.type.declaration.field.context.FieldContext;
import byteback.syntax.type.declaration.field.walker.FieldWalker;
import soot.Scene;
import soot.SootClass;
import soot.SootField;

/**
 * Transformer for a single class field.
 *
 * @author paganma
 */
public abstract class FieldTransformer extends FieldWalker<SceneContext, ClassContext, FieldContext> {

    @Override
    public SceneContext makeSceneContext(final Scene scene) {
        return new SceneContext(scene);
    }

    @Override
    public ClassContext makeClassContext(final SceneContext sceneContext, final SootClass sootClass) {
        return new ClassContext(sceneContext, sootClass);
    }

    @Override
    public FieldContext makeFieldContext(final ClassContext classContext, final SootField sootField) {
        return new FieldContext(classContext, sootField);
    }

}
