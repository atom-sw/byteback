package byteback.syntax.scene.type.declaration.member.field.walker;

import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.scene.type.declaration.member.field.context.FieldContext;
import byteback.syntax.scene.type.declaration.context.ClassContext;
import byteback.syntax.scene.type.declaration.walker.ClassWalker;
import soot.SootClass;
import soot.SootField;
import soot.util.Chain;

import java.util.Iterator;

/**
 * Walks a single field.
 *
 * @param <S> The type of the SceneContext.
 * @param <C> The type of the ClassContext.
 * @param <F> The type of the FieldContext.
 * @author paganma
 */
public abstract class FieldWalker<S extends SceneContext, C extends ClassContext<?>, F extends FieldContext<?>>
        extends ClassWalker<S, C> {

    /**
     * Creates a new {@link FieldContext}.
     *
     * @param classContext The enclosing class context.
     * @param sootField    The field of the context.
     * @return A new field context.
     */
    public abstract F makeFieldContext(final C classContext, final SootField sootField);

    /**
     * Walks a field context.
     *
     * @param fieldContext The field context created with {@link #makeFieldContext}`.
     */
    public abstract void walkField(final F fieldContext);

    @Override
    public void walkClass(final C classContext) {
        final SootClass sootClass = classContext.getSootClass();

        if (sootClass.resolvingLevel() >= SootClass.SIGNATURES) {
            final Chain<SootField> fields = sootClass.getFields();
            final Iterator<SootField> fieldIterator = fields.snapshotIterator();

            while (fieldIterator.hasNext()) {
                final SootField sootField = fieldIterator.next();
                final F fieldContext = makeFieldContext(classContext, sootField);
                walkField(fieldContext);
            }
        }
    }

}
