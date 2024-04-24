package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.ReturnRef;
import byteback.syntax.scene.type.declaration.member.method.body.value.VoidConstant;
import byteback.syntax.scene.type.declaration.member.method.context.MethodContext;
import byteback.syntax.scene.type.declaration.member.method.tag.*;
import byteback.syntax.tag.AnnotationTagReader;
import com.google.common.base.Optional;
import soot.*;
import soot.asm.AsmUtil;
import soot.jimple.Jimple;
import soot.tagkit.AnnotationClassElem;
import soot.tagkit.AnnotationStringElem;

import java.util.ArrayList;
import java.util.List;

public class MethodConditionsTagger extends MethodTransformer {

    private static final Lazy<MethodConditionsTagger> INSTANCE = Lazy.from(MethodConditionsTagger::new);

    public static MethodConditionsTagger v() {
        return INSTANCE.get();
    }

    private MethodConditionsTagger() {
    }

    @Override
    public void transformMethod(final MethodContext methodContext) {
        final SootClass sootClass = methodContext.getClassContext().getSootClass();

        if (sootClass.resolvingLevel() < SootClass.SIGNATURES) {
            return;
        }

        final SootMethod targetMethod = methodContext.getSootMethod();

        AnnotationTagReader.v().getAnnotations(targetMethod)
                .forEach((annotationTag) -> {
                    final String annotationType = annotationTag.getType();

                    switch (annotationType) {
                        case (BBLibNames.REQUIRE_ANNOTATION) -> {
                            AnnotationStringElem annotationStringElement =
                                    AnnotationTagReader.v()
                                            .getValue(annotationTag, AnnotationStringElem.class)
                                            .orElseThrow();
                            final String behaviorName = annotationStringElement.getValue();
                            final SootMethod behaviorMethod = PreconditionResolver.v().resolveBehavior(targetMethod, behaviorName);
                            final ParameterLocalsTag parameterLocalsTag = ParameterLocalsTagProvider.v().getOrThrow(targetMethod);
                            final var parameterLocals = new ArrayList<Value>(parameterLocalsTag.getValues());
                            final PreconditionsTag preconditionsTag = PreconditionsTagProvider.v().getOrCompute(targetMethod);

                            preconditionsTag.getValues()
                                    .add(Vimp.v().newCallExpr(behaviorMethod.makeRef(), parameterLocals));
                        }
                        case (BBLibNames.ENSURE_ANNOTATION) -> {
                            AnnotationStringElem annotationStringElement =
                                    AnnotationTagReader.v()
                                            .getValue(annotationTag, AnnotationStringElem.class)
                                            .orElseThrow();
                            final String behaviorName = annotationStringElement.getValue();
                            final SootMethod behaviorMethod = PostconditionResolver.v().resolveBehavior(targetMethod, behaviorName);
                            final ParameterLocalsTag parameterLocalsTag = ParameterLocalsTagProvider.v().getOrThrow(targetMethod);
                            final List<Local> parameterLocals = parameterLocalsTag.getValues();
                            final var behaviorParameters = new ArrayList<Value>(parameterLocals);
                            final Type returnType = targetMethod.getReturnType();

                            if (returnType != VoidType.v()) {
                                final ReturnRef returnRef = Vimp.v().newReturnRef(returnType);
                                behaviorParameters.add(returnRef);
                            }

                            final PostconditionsTag postconditionsTag = PostconditionsTagProvider.v().getOrCompute(targetMethod);

                            postconditionsTag.getValues()
                                    .add(Vimp.v().newCallExpr(behaviorMethod.makeRef(), behaviorParameters));
                        }
                        case (BBLibNames.RETURN_ANNOTATION) -> {
                            AnnotationTagReader.v()
                                    .getElement(annotationTag, "when", AnnotationStringElem.class)
                                    .ifPresentOrElse(
                                            annotationStringElement -> {
                                                final String behaviorName = annotationStringElement.getValue();
                                                final SootMethod behaviorMethod = PreconditionResolver.v().resolveBehavior(targetMethod, behaviorName);
                                                final ParameterLocalsTag parameterLocalsTag = ParameterLocalsTagProvider.v().getOrThrow(targetMethod);
                                                final var parameterLocals = new ArrayList<Value>(parameterLocalsTag.getValues());
                                                final PostconditionsTag postconditionsTag = PostconditionsTagProvider.v().getOrCompute(targetMethod);
                                                postconditionsTag.getValues()
                                                        .add(Vimp.v().newImpliesExpr(
                                                                Vimp.v().nest(
                                                                        Vimp.v().newCallExpr(
                                                                                behaviorMethod.makeRef(),
                                                                                parameterLocals
                                                                        )
                                                                ),
                                                                Vimp.v().nest(
                                                                        Jimple.v().newEqExpr(
                                                                                Vimp.v().newCaughtExceptionRef(),
                                                                                VoidConstant.v()
                                                                        )
                                                                )
                                                        ));
                                            },
                                            () -> {
                                                final PostconditionsTag postconditionsTag =
                                                        PostconditionsTagProvider.v()
                                                                .getOrCompute(targetMethod);
                                                postconditionsTag.getValues()
                                                        .add(Jimple.v().newEqExpr(
                                                                Vimp.v().newCaughtExceptionRef(),
                                                                VoidConstant.v()
                                                        ));
                                            }
                                    );
                        }
                        case (BBLibNames.RAISE_ANNOTATION) -> {
                            AnnotationTagReader.v().getElement(
                                    annotationTag,
                                    "when",
                                    AnnotationStringElem.class
                            ).ifPresent(annotationStringElement -> {
                                        final String behaviorName = annotationStringElement.getValue();
                                        final SootMethod behaviorMethod = PreconditionResolver.v().resolveBehavior(targetMethod, behaviorName);
                                        AnnotationTagReader.v().getElement(
                                                annotationTag,
                                                "exception",
                                                AnnotationClassElem.class
                                        ).ifPresent((annotationClassElem) -> {
                                            final PostconditionsTag postconditionsTag =
                                                    PostconditionsTagProvider.v()
                                                            .getOrCompute(targetMethod);
                                            final Type exceptionClassType = AsmUtil.toBaseType(annotationClassElem.getDesc(), Optional.absent());
                                            final ParameterLocalsTag parameterLocalsTag = ParameterLocalsTagProvider.v().getOrThrow(targetMethod);
                                            final var parameterLocals = new ArrayList<Value>(parameterLocalsTag.getValues());

                                            postconditionsTag.getValues()
                                                    .add(Vimp.v().newImpliesExpr(
                                                            Vimp.v().nest(
                                                                    Vimp.v().newCallExpr(
                                                                            behaviorMethod.makeRef(),
                                                                            parameterLocals
                                                                    )
                                                            ),
                                                            Vimp.v().nest(
                                                                    Jimple.v().newInstanceOfExpr(
                                                                            Vimp.v().newCaughtExceptionRef(),
                                                                            exceptionClassType
                                                                    )
                                                            )
                                                    ));
                                        });
                                    }
                            );
                        }
                    }
                });
    }

}
