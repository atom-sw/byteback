package byteback.syntax.scene.type.declaration.member.field.transformer;

import byteback.syntax.scene.type.declaration.context.ClassContext;
import byteback.syntax.scene.type.declaration.member.field.context.FieldContext;
import byteback.syntax.scene.type.declaration.transformer.ClassTransformer;
import soot.SootClass;
import soot.SootField;
import soot.util.Chain;

import java.util.Iterator;

public abstract class FieldTransformer extends ClassTransformer {

    public abstract void transformField(final FieldContext fieldContext);

    @Override
    public void transformClass(final ClassContext classContext) {
        final SootClass sootClass = classContext.getSootClass();

        if (sootClass.resolvingLevel() >= SootClass.SIGNATURES) {
            final Chain<SootField> fields = sootClass.getFields();
            final Iterator<SootField> fieldIterator = fields.snapshotIterator();

            while (fieldIterator.hasNext()) {
                final SootField sootField = fieldIterator.next();
                final var fieldContext = new FieldContext(classContext, sootField);
                transformField(fieldContext);
            }
        }
    }

}
