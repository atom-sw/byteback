package byteback.syntax.tag;

import byteback.common.function.Lazy;
import soot.tagkit.*;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Reads the VisibilityAnnotationTag from a given host.
 *
 * @author paganma
 */
public class AnnotationReader extends TagReader<Host, VisibilityAnnotationTag> {

    private static final Lazy<AnnotationReader> instance =
            Lazy.from(()  -> new AnnotationReader("VisibilityAnnotationTag"));

    public static final String VALUE_IDENTIFIER = "value";

    public static AnnotationReader v() {
        return instance.get();
    }

    private AnnotationReader(final String tagName) {
        super(tagName);
    }

    public Optional<AnnotationElem> getValue(final AnnotationTag annotationTag) {
        return getElem(annotationTag, VALUE_IDENTIFIER);
    }

    public Optional<AnnotationElem> getElem(final AnnotationTag annotationTag, final String identifier) {
        return annotationTag.getElems().stream().filter((elem) -> elem.getName().equals(identifier)).findFirst();
    }

    private void getAnnotations(final Stream.Builder<AnnotationTag> builder, final AnnotationElem annotationElement) {
        if (annotationElement instanceof final AnnotationArrayElem annotationArrayElement) {
            for (AnnotationElem value : annotationArrayElement.getValues()) {
                getAnnotations(builder, value);
            }
        } else if (annotationElement instanceof final AnnotationAnnotationElem annotationAnnotationElement) {
            final AnnotationTag tag = annotationAnnotationElement.getValue();
            builder.accept(tag);
            getValue(tag).ifPresent((elem) -> getAnnotations(builder, elem));
        }
    }

    public Stream<AnnotationTag> getAnnotations(final AnnotationTag annotationTag) {
        final Stream.Builder<AnnotationTag> builder = Stream.builder();
        builder.add(annotationTag);
        getValue(annotationTag).ifPresent((elem) -> {
            getAnnotations(builder, elem);
        });

        return builder.build();
    }

    public Stream<AnnotationTag> getAnnotations(final Host host) {
        return get(host).stream()
                .flatMap((visibilityAnnotationTag) ->
                        visibilityAnnotationTag.getAnnotations().stream()
                                .flatMap(this::getAnnotations));
    }

    public Optional<AnnotationTag> getAnnotation(final Host host, final String name) {
        return getAnnotations(host)
                .filter((tag) -> tag.getType().equals(name))
                .findFirst();
    }

    public boolean hasAnnotation(final Host host, final String name) {
        return getAnnotation(host, name)
                .isPresent();
    }

}
