package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.context.MethodContext;
import byteback.syntax.scene.type.declaration.member.method.tag.*;
import byteback.syntax.tag.AnnotationTagReader;
import soot.SootMethod;
import soot.tagkit.AnnotationStringElem;
import soot.tagkit.AnnotationTag;

public class MethodTypeTagger extends MethodTransformer {

    private static final Lazy<MethodTypeTagger> INSTANCE = Lazy.from(MethodTypeTagger::new);

    public static MethodTypeTagger v() {
        return INSTANCE.get();
    }

    private MethodTypeTagger() {
    }

    @Override
    public void transformMethod(final MethodContext methodContext) {
        final SootMethod sootMethod = methodContext.getSootMethod();

        AnnotationTagReader.v().getAnnotations(sootMethod)
                .forEach((annotationTag) -> {
                    final String annotationType = annotationTag.getType();

                    switch (annotationType) {
                        case BBLibNames.BEHAVIOR_ANNOTATION ->
                                BehaviorTagFlagger.v().flag(sootMethod);
                        case BBLibNames.OPERATOR_ANNOTATION ->
                                OperatorTagFlagger.v().flag(sootMethod);
                        case BBLibNames.TWOSTATE_ANNOTATION ->
                                TwoStateTagFlagger.v().flag(sootMethod);
                        case BBLibNames.EXCEPTIONAL_ANNOTATION ->
                                ExceptionalTagFlagger.v().flag(sootMethod);
                        case BBLibNames.PRELUDE_ANNOTATION -> {
                            final var annotationStringElement =
                                    AnnotationTagReader.v().getValue(annotationTag, AnnotationStringElem.class)
                                            .orElseThrow();
                            final var preludeDefinitionTag = new PreludeTag(annotationStringElement.getValue());
                            PreludeTagProvider.v().put(sootMethod, preludeDefinitionTag);
                        }
                    }
                });
    }

}
