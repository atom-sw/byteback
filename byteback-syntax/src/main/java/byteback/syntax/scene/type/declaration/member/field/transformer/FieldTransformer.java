package byteback.syntax.scene.type.declaration.member.field.transformer;

import byteback.syntax.scene.type.declaration.member.field.transformer.context.FieldTransformerContext;
import byteback.syntax.scene.type.declaration.transformer.ClassTransformer;
import byteback.syntax.scene.type.declaration.transformer.context.ClassTransformationContext;
import soot.SootClass;
import soot.SootField;
import soot.util.Chain;

import java.util.Iterator;

public abstract class FieldTransformer extends ClassTransformer {

    public abstract void transformField(final FieldTransformerContext fieldContext);

    @Override
    public void transformClass(final ClassTransformationContext classTransformationContext) {
        final SootClass sootClass = classTransformationContext.getSootClass();

        if (sootClass.resolvingLevel() >= SootClass.SIGNATURES) {
            final Chain<SootField> fields = sootClass.getFields();
            final Iterator<SootField> fieldIterator = fields.snapshotIterator();

            while (fieldIterator.hasNext()) {
                final SootField sootField = fieldIterator.next();
                final var fieldContext = new FieldTransformerContext(classTransformationContext, sootField);
                transformField(fieldContext);
            }
        }
    }

}
