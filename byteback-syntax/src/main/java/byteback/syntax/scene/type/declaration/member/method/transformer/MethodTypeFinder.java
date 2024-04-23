package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.context.MethodContext;
import byteback.syntax.scene.type.declaration.member.method.tag.*;
import byteback.syntax.tag.AnnotationTagReader;
import soot.SootMethod;
import soot.tagkit.AnnotationStringElem;
import soot.tagkit.AnnotationTag;

public class MethodTypeFinder extends MethodTransformer {

    private static final Lazy<MethodTypeFinder> INSTANCE = Lazy.from(MethodTypeFinder::new);

    public static MethodTypeFinder v() {
        return INSTANCE.get();
    }

    private MethodTypeFinder() {
    }

    public void handleAnnotation(final SootMethod sootMethod, final AnnotationTag annotationTag) {
        if (annotationTag.getType().equals(BBLibNames.BEHAVIOR_ANNOTATION)) {
            BehaviorTagFlagger.v().flag(sootMethod);
        }

        if (annotationTag.getType().equals(BBLibNames.OPERATOR_ANNOTATION)) {
            OperatorTagFlagger.v().flag(sootMethod);
        }

        if (annotationTag.getType().equals(BBLibNames.TWOSTATE_ANNOTATION)) {
            TwoStateTagFlagger.v().flag(sootMethod);
        }

        if (annotationTag.getType().equals(BBLibNames.EXCEPTIONAL_ANNOTATION)) {
            ExceptionalTagFlagger.v().flag(sootMethod);
        }

        if (annotationTag.getType().equals(BBLibNames.PRELUDE_ANNOTATION)) {
            final var annotationStringElement =
                    AnnotationTagReader.v().getValue(annotationTag, AnnotationStringElem.class)
                    .orElseThrow();
            final var preludeDefinitionTag = new PreludeTag(annotationStringElement.getValue());
            PreludeTagProvider.v().put(sootMethod, preludeDefinitionTag);
        }
    }

    @Override
    public void transformMethod(final MethodContext methodContext) {
        final SootMethod sootMethod = methodContext.getSootMethod();

        AnnotationTagReader.v().getAnnotations(sootMethod)
                .forEach((annotationTag) -> {
                    handleAnnotation(sootMethod, annotationTag);
                });
    }

}
