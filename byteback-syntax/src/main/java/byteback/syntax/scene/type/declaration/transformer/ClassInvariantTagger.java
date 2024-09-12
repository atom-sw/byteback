package byteback.syntax.scene.type.declaration.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.tag.InvariantsTag;
import byteback.syntax.scene.type.declaration.tag.InvariantsTagAccessor;
import byteback.syntax.tag.AnnotationTagReader;
import soot.SootClass;
import soot.Value;
import soot.tagkit.AnnotationStringElem;

public class ClassInvariantTagger extends ClassTransformer {

	private static final Lazy<ClassInvariantTagger> INSTANCE = Lazy.from(ClassInvariantTagger::new);

	public static ClassInvariantTagger v() {
		return INSTANCE.get();
	}

	@Override
	public void transformClass(final SootClass sootClass) {
		AnnotationTagReader.v().getAnnotation(sootClass, BBLibNames.CLASS_INVARIANT_ANNOTATION)
				.flatMap(annotationTag -> AnnotationTagReader.v().getValue(annotationTag, AnnotationStringElem.class))
				.ifPresent(annotationStringElement -> {
			final String behaviorName = annotationStringElement.getValue();
			final Value behaviorValue = InvariantBehaviorResolver.v().resolveBehavior(sootClass, behaviorName);
			final var invariantsTag = InvariantsTagAccessor.v().putIfAbsent(sootClass, InvariantsTag::new);
			invariantsTag.addCondition(behaviorValue);
		});
	}

}
