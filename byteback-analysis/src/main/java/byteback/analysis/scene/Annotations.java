package byteback.analysis.scene;

import byteback.common.function.Lazy;
import soot.tag.AnnotationArrayElement;
import soot.tag.AnnotationElement;
import soot.tag.AnnotationSubElement;
import soot.tag.AnnotationTag;

import java.util.Optional;
import java.util.stream.Stream;

public class Annotations {

    private static final Lazy<Annotations> instance = Lazy.from(Annotations::new);

    public static Annotations v() {
        return instance.get();
    }

    private Annotations() {
    }

    private static final String VALUE_IDENTIFIER = "value";

    public Optional<AnnotationElement> getValue(final AnnotationTag tag) {
        return getElem(tag, VALUE_IDENTIFIER);
    }

    public Optional<AnnotationElement> getElem(final AnnotationTag tag, final String identifier) {
        return tag.getElems().stream().filter((elem) -> elem.getName().equals(identifier)).findFirst();
    }

    private void getAnnotations(final Stream.Builder<AnnotationTag> builder, final AnnotationElement element) {
        element.apply(new AbtractAnnotationElementSwitch<>() {

            @Override
            public void caseAnnotationArrayElem(final AnnotationArrayElement element) {
                for (AnnotationElement value : element.getValues()) {
                    getAnnotations(builder, value);
                }
            }

            @Override
            public void caseAnnotationAnnotationElem(final AnnotationSubElement element) {
                final AnnotationTag tag = element.getValue();
                builder.accept(tag);
                getValue(tag).ifPresent((elem) -> getAnnotations(builder, elem));
            }

        });
    }

    public Stream<AnnotationTag> getAnnotations(final AnnotationTag tag) {
        Stream.Builder<AnnotationTag> builder = Stream.builder();
        builder.add(tag);
        getValue(tag).ifPresent((elem) -> {
            getAnnotations(builder, elem);
        });

        return builder.build();
    }

}
