package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.tag.AnnotationReader;
import byteback.syntax.tag.TagReader;
import soot.SootMethod;
import soot.tagkit.AnnotationStringElem;

import java.util.Optional;

public class PreludeDefinitionReader extends TagReader<SootMethod, PreludeDefinitionTag> {

    private static final Lazy<PreludeDefinitionReader> INSTANCE =
            Lazy.from(() -> new PreludeDefinitionReader(PreludeDefinitionTag.NAME));

    public static PreludeDefinitionReader v() {
        return INSTANCE.get();
    }

    private PreludeDefinitionReader(final String tagName) {
        super(tagName);
    }

    @Override
    public Optional<PreludeDefinitionTag> get(final SootMethod sootMethod) {
        return AnnotationReader.v().getAnnotation(sootMethod, BBLibNames.PRELUDE_ANNOTATION)
                .flatMap(annotationTag ->
                        AnnotationReader.v().getValue(annotationTag)
                                .flatMap(annotationElement -> {
                                    if (annotationElement instanceof final AnnotationStringElem annotationStringElement) {
                                        return Optional.of(new PreludeDefinitionTag(annotationStringElement.getValue()));
                                    }

                                    return Optional.empty();
                                }));
    }

}