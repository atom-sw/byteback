package byteback.syntax.type.declaration.field.walker;

import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.type.declaration.context.ClassContext;
import byteback.syntax.type.declaration.field.context.FieldContext;
import byteback.syntax.type.declaration.walker.ClassWalker;
import soot.SootClass;
import soot.SootField;
import soot.util.Chain;

import java.util.Iterator;

public abstract class FieldWalker<S extends SceneContext, C extends ClassContext, F extends FieldContext>
        extends ClassWalker<S, C> {

    public abstract F makeFieldContext(final ClassContext classContext, final SootField sootField);

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
