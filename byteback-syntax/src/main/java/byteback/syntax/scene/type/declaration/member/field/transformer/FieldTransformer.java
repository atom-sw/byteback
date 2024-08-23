package byteback.syntax.scene.type.declaration.member.field.transformer;

import byteback.syntax.scene.type.declaration.transformer.ClassTransformer;
import soot.SootClass;
import soot.SootField;
import soot.util.Chain;

import java.util.Iterator;

public abstract class FieldTransformer extends ClassTransformer {

	public abstract void transformField(final SootField sootField);

	@Override
	public void transformClass(final SootClass sootClass) {
		if (sootClass.resolvingLevel() >= SootClass.SIGNATURES) {
			final Chain<SootField> fields = sootClass.getFields();
			final Iterator<SootField> fieldIterator = fields.snapshotIterator();

			while (fieldIterator.hasNext()) {
				final SootField sootField = fieldIterator.next();
				transformField(sootField);
			}
		}
	}

}
