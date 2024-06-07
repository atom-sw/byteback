
package byteback.syntax.scene.type.declaration.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.body.value.box.tag.FreeTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTagAccessor;
import byteback.syntax.tag.AnnotationTagReader;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.grimp.internal.ExprBox;
import soot.tagkit.AnnotationStringElem;

public class ClassInvariantTagger extends ClassTransformer {

	private static final Lazy<ClassInvariantTagger> INSTANCE = Lazy.from(ClassInvariantTagger::new);

	public static ClassInvariantTagger v() {
		return INSTANCE.get();
	}

	@Override
	public void transformClass(final Scene scene, final SootClass sootClass) {
		AnnotationTagReader.v().getAnnotation(sootClass, BBLibNames.CLASS_INVARIANT_ANNOTATION)
				.flatMap(annotationTag -> AnnotationTagReader.v().getValue(annotationTag, AnnotationStringElem.class))
				.ifPresent(annotationStringElement -> {
			final String behaviorName = annotationStringElement.getValue();
			final Value behaviorValue = InvariantBehaviorResolver.v().resolveBehavior(sootClass, behaviorName);

			for (final SootMethod sootMethod : sootClass.getMethods()) {
				final var preconditionBox = new ExprBox(behaviorValue);
				FreeTagMarker.v().flag(preconditionBox);
				final var postconditionBox = new ExprBox(behaviorValue);

				PreconditionsTagAccessor.v()
						.putIfAbsent(sootMethod, PreconditionsTag::new)
						.addConditionBox(preconditionBox);
				PostconditionsTagAccessor.v()
						.putIfAbsent(sootMethod, PostconditionsTag::new)
						.addConditionBox(postconditionBox);
			}
		});
	}

}
