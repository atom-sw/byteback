package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.VoidConstant;
import byteback.syntax.scene.type.declaration.member.method.context.MethodContext;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTagProvider;
import byteback.syntax.tag.AnnotationTagReader;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.Jimple;
import soot.tagkit.AnnotationStringElem;

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
                            PreconditionsResolver.v().resolveCondition(targetMethod, behaviorName);
                        }
                        case (BBLibNames.ENSURE_ANNOTATION) -> {
                            AnnotationStringElem annotationStringElement =
                                    AnnotationTagReader.v()
                                            .getValue(annotationTag, AnnotationStringElem.class)
                                            .orElseThrow();
                            final String behaviorName = annotationStringElement.getValue();
                            PostconditionsResolver.v().resolveCondition(targetMethod, behaviorName);
                        }
                        case (BBLibNames.RETURN_ANNOTATION) -> {
                            AnnotationTagReader.v()
                                    .getElement(annotationTag, "when", AnnotationStringElem.class)
                                    .ifPresentOrElse(
                                            annotationStringElem -> {
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
                    }
                });
    }

}
