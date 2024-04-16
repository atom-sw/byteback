package byteback.syntax.scene.type.declaration.member.field.transformer;

import byteback.syntax.scene.transformer.context.SceneTransformerContext;
import byteback.syntax.scene.type.declaration.member.field.transformer.context.FieldTransformerContext;
import byteback.syntax.scene.type.declaration.member.field.walker.FieldWalker;
import byteback.syntax.scene.type.declaration.transformer.context.ClassTransformerContext;
import soot.Scene;
import soot.SootClass;
import soot.SootField;

/**
 * Transformer for a single class field.
 *
 * @author paganma
 */
public abstract class FieldTransformer
        extends FieldWalker<SceneTransformerContext, ClassTransformerContext, FieldTransformerContext> {

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
    public FieldTransformerContext makeFieldContext(final ClassTransformerContext classContext,
                                                    final SootField sootField) {
        return new FieldTransformerContext(classContext, sootField);
    }

}
