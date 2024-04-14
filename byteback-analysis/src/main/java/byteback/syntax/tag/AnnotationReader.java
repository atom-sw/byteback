package byteback.syntax.tag;

import byteback.common.function.Lazy;
import soot.tagkit.*;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Reads a VisibilityAnnotationTag from a host.
 *
 * @author paganma
 */
public class AnnotationReader extends TagReader<Host, VisibilityAnnotationTag> {

    private static final Lazy<AnnotationReader> INSTANCE =
            Lazy.from(() -> new AnnotationReader(VisibilityAnnotationTag.NAME));

    public static AnnotationReader v() {
        return INSTANCE.get();
    }

    private AnnotationReader(final String tagName) {
        super(tagName);
    }

    public static final String VALUE_IDENTIFIER = "value";

    public Optional<AnnotationElem> getValue(final AnnotationTag annotationTag) {
        return getElement(annotationTag, VALUE_IDENTIFIER);
    }

    public Optional<AnnotationElem> getElement(final AnnotationTag annotationTag, final String identifier) {
        return annotationTag.getElems().stream()
                .filter((element) ->
                        element.getName().equals(identifier))
                .findFirst();
    }

    private void getAnnotations(final Stream.Builder<AnnotationTag> builder, final AnnotationElem annotationElement) {
        if (annotationElement instanceof final AnnotationArrayElem annotationArrayElement) {
            for (AnnotationElem value : annotationArrayElement.getValues()) {
                getAnnotations(builder, value);
            }
        } else if (annotationElement instanceof final AnnotationAnnotationElem annotationAnnotationElement) {
            final AnnotationTag tag = annotationAnnotationElement.getValue();
            builder.accept(tag);
            getValue(tag)
                    .ifPresent((subElement) ->
                            getAnnotations(builder, subElement));
        }
    }

    public Stream<AnnotationTag> getAnnotations(final AnnotationTag annotationTag) {
        final Stream.Builder<AnnotationTag> builder = Stream.builder();
        builder.add(annotationTag);
        getValue(annotationTag)
                .ifPresent((annotationElement) ->
                        getAnnotations(builder, annotationElement));

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
        return getAnnotation(host, name).isPresent();
    }

}
