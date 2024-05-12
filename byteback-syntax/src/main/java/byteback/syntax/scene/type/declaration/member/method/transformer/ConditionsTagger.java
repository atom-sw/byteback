package byteback.syntax.scene.type.declaration.member.method.transformer;

import com.google.common.base.Optional;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.UnitConstant;
import byteback.syntax.scene.type.declaration.member.method.context.MethodContext;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTagAccessor;
import byteback.syntax.tag.AnnotationTagReader;
import soot.*;
import soot.asm.AsmUtil;
import soot.jimple.Jimple;
import soot.tagkit.AnnotationClassElem;
import soot.tagkit.AnnotationStringElem;

public class ConditionsTagger extends MethodTransformer {

	private static final Lazy<ConditionsTagger> INSTANCE = Lazy.from(ConditionsTagger::new);

	public static ConditionsTagger v() {
		return INSTANCE.get();
	}

	private ConditionsTagger() {
	}

	@Override
	public void transformMethod(final MethodContext methodContext) {
		final SootClass sootClass = methodContext.getClassContext().getSootClass();

		if (sootClass.resolvingLevel() < SootClass.SIGNATURES) {
			return;
		}

		final SootMethod targetMethod = methodContext.getSootMethod();

		AnnotationTagReader.v().getAnnotations(targetMethod).forEach((annotationTag) -> {
			final String annotationType = annotationTag.getType();

			switch (annotationType) {
				case (BBLibNames.REQUIRE_ANNOTATION) -> {
					final PreconditionsTag preconditionsTag = PreconditionsTagAccessor.v()
							.putIfAbsent(targetMethod, PreconditionsTag::new);
					AnnotationTagReader.v().getValue(annotationTag, AnnotationStringElem.class)
							.ifPresent(annotationStringElement -> {
								final String behaviorName = annotationStringElement.getValue();
								final Value condition = PreBehaviorResolver.v().resolveBehavior(targetMethod, behaviorName);
								preconditionsTag.addCondition(condition);
							});
				}
				case (BBLibNames.ENSURE_ANNOTATION) -> {
					final PostconditionsTag postconditionsTag = PostconditionsTagAccessor.v()
							.putIfAbsent(targetMethod, PostconditionsTag::new);
					AnnotationTagReader.v().getValue(annotationTag, AnnotationStringElem.class)
							.ifPresent(annotationStringElement -> {
								final String behaviorName = annotationStringElement.getValue();
								final Value condition = PostBehaviorResolver.v().resolveBehavior(targetMethod, behaviorName);
								postconditionsTag.addCondition(condition);
							});
				}
				case (BBLibNames.RETURN_ANNOTATION) -> {
					final PostconditionsTag postconditionsTag = PostconditionsTagAccessor.v()
							.putIfAbsent(targetMethod, PostconditionsTag::new);
					AnnotationTagReader.v().getElement(annotationTag, "when", AnnotationStringElem.class)
							.ifPresentOrElse((annotationStringElement) -> {
								final String behaviorName = annotationStringElement.getValue();
								final Value behaviorCall = PreBehaviorResolver.v().resolveBehavior(targetMethod, behaviorName);
								final Value condition = Vimp.v().newImpliesExpr(
										Vimp.v().nest(
												Vimp.v().newOldExpr(behaviorCall)),
										Vimp.v().nest(
												Jimple.v().newEqExpr(Vimp.v().nest(
														Vimp.v().newThrownRef()),
														UnitConstant.v())));
								postconditionsTag.addCondition(condition);
							}, () -> {
								final Value condition = Jimple.v().newEqExpr(
										Vimp.v().nest(Vimp.v().newThrownRef()),
										UnitConstant.v());
								postconditionsTag.addCondition(condition);
							});
				}
				case (BBLibNames.RAISE_ANNOTATION) -> {
					final PostconditionsTag postconditionsTag = PostconditionsTagAccessor.v()
							.putIfAbsent(targetMethod, PostconditionsTag::new);
					AnnotationTagReader.v().getElement(
							annotationTag,
							"when",
							AnnotationStringElem.class)
							.ifPresent((annotationStringElement) -> {
								AnnotationTagReader.v().getElement(
										annotationTag,
										"exception",
										AnnotationClassElem.class)
										.ifPresent((classElement) -> {
											final String exceptionDesc = classElement.getDesc();
											final RefType exceptionType = (RefType) AsmUtil.toJimpleType(exceptionDesc, Optional.absent());
											final String behaviorName = annotationStringElement.getValue();
											final Value behaviorCall = PreBehaviorResolver.v().resolveBehavior(
													targetMethod, behaviorName);
											final Value condition = Vimp.v().newImpliesExpr(
													Vimp.v().nest(
															Vimp.v().newOldExpr(behaviorCall)),
													Vimp.v().nest(
															Jimple.v().newInstanceOfExpr(
																	Vimp.v().nest(Vimp.v().newThrownRef()),
																	exceptionType)));
											postconditionsTag.addCondition(condition);
										});
							});
				}
			}
		});
	}

}
