package byteback.syntax.scene.type.declaration.member.method.transformer;

import com.google.common.base.Optional;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.UnitConstant;
import byteback.syntax.scene.type.declaration.member.method.tag.OnlyPostconditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.OnlyPostconditionsTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.OnlyPreconditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.OnlyPreconditionsTagAccessor;
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
	public void transformMethod(final SootMethod sootMethod) {
		if (sootMethod.getDeclaringClass().resolvingLevel() < SootClass.SIGNATURES) {
			return;
		}

		AnnotationTagReader.v().getAnnotations(sootMethod).forEach((annotationTag) -> {
			final String annotationType = annotationTag.getType();

			switch (annotationType) {
				case (BBLibNames.REQUIRE_ANNOTATION) -> {
					final PreconditionsTag preconditionsTag = PreconditionsTagAccessor.v()
							.putIfAbsent(sootMethod, PreconditionsTag::new);
					AnnotationTagReader.v().getValue(annotationTag, AnnotationStringElem.class)
							.ifPresent(annotationStringElement -> {
								final String behaviorName = annotationStringElement.getValue();
								final Value condition = PreBehaviorResolver.v().resolveBehavior(sootMethod, behaviorName);
								preconditionsTag.addCondition(condition);
							});
				}
				case (BBLibNames.ENSURE_ANNOTATION) -> {
					final PostconditionsTag postconditionsTag = PostconditionsTagAccessor.v()
							.putIfAbsent(sootMethod, PostconditionsTag::new);
					AnnotationTagReader.v().getValue(annotationTag, AnnotationStringElem.class)
							.ifPresent(annotationStringElement -> {
								final String behaviorName = annotationStringElement.getValue();
								final Value condition = PostBehaviorResolver.v().resolveBehavior(sootMethod, behaviorName);
								postconditionsTag.addCondition(condition);
							});
				}
				case (BBLibNames.RETURN_ANNOTATION) -> {
					final PostconditionsTag postconditionsTag = PostconditionsTagAccessor.v()
							.putIfAbsent(sootMethod, PostconditionsTag::new);
					AnnotationTagReader.v().getElement(annotationTag, "when", AnnotationStringElem.class)
							.ifPresentOrElse((annotationStringElement) -> {
								final String behaviorName = annotationStringElement.getValue();
								final Value behaviorCall = PreBehaviorResolver.v().resolveBehavior(sootMethod, behaviorName);
								final Value condition = Vimp.v().newImpliesExpr(
										Vimp.v().nest(
												Vimp.v().newOldExpr(
														Vimp.v().nest(behaviorCall))),
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
							.putIfAbsent(sootMethod, PostconditionsTag::new);

					AnnotationTagReader.v().getElement(
							annotationTag,
							"exception",
							AnnotationClassElem.class)
							.ifPresentOrElse((classElement) -> {
								AnnotationTagReader.v().getElement(
										annotationTag,
										"when",
										AnnotationStringElem.class)
										.ifPresentOrElse((annotationStringElement) -> {
											final String exceptionDesc = classElement.getDesc();
											final RefType exceptionType = (RefType) AsmUtil.toJimpleType(exceptionDesc, Optional.absent());
											final String behaviorName = annotationStringElement.getValue();
											final Value behaviorCall = PreBehaviorResolver.v().resolveBehavior(
													sootMethod, behaviorName);
											final Value condition = Vimp.v().newImpliesExpr(
													Vimp.v().nest(
															Vimp.v().newOldExpr(Vimp.v().nest(behaviorCall))),
													Vimp.v().nest(
															Jimple.v().newInstanceOfExpr(
																	Vimp.v().nest(Vimp.v().newThrownRef()),
																	exceptionType)));
											postconditionsTag.addCondition(condition);
										}, () -> {
											final String exceptionDesc = classElement.getDesc();
											final RefType exceptionType = (RefType) AsmUtil.toJimpleType(exceptionDesc, Optional.absent());
											final Value condition = Jimple.v().newInstanceOfExpr(
													Vimp.v().nest(Vimp.v().newThrownRef()),
													exceptionType);
											postconditionsTag.addCondition(condition);
										});
							}, () -> {
								AnnotationTagReader.v().getElement(
										annotationTag,
										"when",
										AnnotationStringElem.class)
										.ifPresentOrElse((annotationStringElement) -> {
											final String behaviorName = annotationStringElement.getValue();
											final Value behaviorCall = PreBehaviorResolver.v().resolveBehavior(
													sootMethod, behaviorName);
											final Value condition = Vimp.v().newImpliesExpr(
													Vimp.v().nest(
															Vimp.v().newOldExpr(Vimp.v().nest(behaviorCall))),
													Vimp.v().nest(
															Jimple.v().newNeExpr(Vimp.v().newThrownRef(), UnitConstant.v())));
											postconditionsTag.addCondition(condition);
										}, () -> {
											final Value condition = Jimple.v().newNeExpr(Vimp.v().nest(Vimp.v().newThrownRef()),
													UnitConstant.v());
											postconditionsTag.addCondition(condition);
										});
							});
				}
				case (BBLibNames.REQUIRE_ONLY_ANNOTATION) -> {
					final OnlyPreconditionsTag onlyPreconditionsTag = OnlyPreconditionsTagAccessor.v()
							.putIfAbsent(sootMethod, OnlyPreconditionsTag::new);
					AnnotationTagReader.v().getValue(annotationTag, AnnotationStringElem.class)
							.ifPresent(annotationStringElement -> {
								final String behaviorName = annotationStringElement.getValue();
								final Value condition = PreBehaviorResolver.v().resolveBehavior(sootMethod, behaviorName);
								onlyPreconditionsTag.addCondition(condition);
							});
				}
				case (BBLibNames.ENSURE_ONLY_ANNOTATION) -> {
					final OnlyPostconditionsTag onlyPostconditionTag = OnlyPostconditionsTagAccessor.v()
							.putIfAbsent(sootMethod, OnlyPostconditionsTag::new);
					AnnotationTagReader.v().getValue(annotationTag, AnnotationStringElem.class)
							.ifPresent(annotationStringElement -> {
								final String behaviorName = annotationStringElement.getValue();
								final Value condition = PreBehaviorResolver.v().resolveBehavior(sootMethod, behaviorName);
								onlyPostconditionTag.addCondition(condition);
							});
				}
			}
		});
	}

}
