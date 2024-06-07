package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.tag.*;
import byteback.syntax.tag.AnnotationTagReader;
import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.tagkit.AnnotationStringElem;

public class ModifierTagger extends MethodTransformer {

	private static final Lazy<ModifierTagger> INSTANCE = Lazy.from(ModifierTagger::new);

	public static ModifierTagger v() {
		return INSTANCE.get();
	}

	private ModifierTagger() {
	}

	@Override
	public void transformMethod(final Scene scene, final SootClass sootClass, final SootMethod sootMethod) {
		AnnotationTagReader.v().getAnnotations(sootMethod)
				.forEach((annotationTag) -> {
					final String annotationType = annotationTag.getType();

					switch (annotationType) {
						case BBLibNames.EXPORT_ANNOTATION ->
							ExportTagMarker.v().flag(sootMethod);
						case BBLibNames.IMPORT_ANNOTATION ->
							ImportTagMarker.v().flag(sootMethod);
						case BBLibNames.ABSTRACT_ANNOTATION ->
							sootMethod.setModifiers(sootMethod.getModifiers() | Modifier.ABSTRACT);
						case BBLibNames.PURE_ANNOTATION ->
							PureTagMarker.v().flag(sootMethod);
						case BBLibNames.IGNORE_ANNOTATION ->
							IgnoreTagMarker.v().flag(sootMethod);
						case BBLibNames.BEHAVIOR_ANNOTATION ->
							BehaviorTagMarker.v().flag(sootMethod);
						case BBLibNames.OPERATOR_ANNOTATION ->
							OperatorTagMarker.v().flag(sootMethod);
						case BBLibNames.TWOSTATE_ANNOTATION ->
							TwoStateTagMarker.v().flag(sootMethod);
						case BBLibNames.EXCEPTIONAL_ANNOTATION ->
							ExceptionalTagMarker.v().flag(sootMethod);
						case BBLibNames.PRELUDE_ANNOTATION -> {
							final var annotationStringElement = AnnotationTagReader.v()
									.getValue(annotationTag, AnnotationStringElem.class)
									.orElseThrow();
							final var preludeDefinitionTag = new PreludeTag(annotationStringElement.getValue());
							PreludeTagAccessor.v().put(sootMethod, preludeDefinitionTag);
						}
					}
				});
	}

}
