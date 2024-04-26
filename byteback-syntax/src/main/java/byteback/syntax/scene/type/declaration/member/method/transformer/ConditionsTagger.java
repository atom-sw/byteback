package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.context.MethodContext;
import byteback.syntax.tag.AnnotationTagReader;
import soot.*;

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

        AnnotationTagReader.v().getAnnotations(targetMethod)
                .forEach((annotationTag) -> {
                    final String annotationType = annotationTag.getType();

                    switch (annotationType) {
                        case (BBLibNames.REQUIRE_ANNOTATION) -> {
                        }
                        case (BBLibNames.ENSURE_ANNOTATION) -> {
                        }
                        case (BBLibNames.RETURN_ANNOTATION) -> {
                        }
                        case (BBLibNames.RAISE_ANNOTATION) -> {
                        }
                    }
                });
    }

}
