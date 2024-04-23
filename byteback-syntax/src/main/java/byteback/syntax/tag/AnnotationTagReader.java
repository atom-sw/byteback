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
public class AnnotationTagReader extends TagReader<Host, VisibilityAnnotationTag> {

    private static final Lazy<AnnotationTagReader> INSTANCE =
            Lazy.from(() -> new AnnotationTagReader(VisibilityAnnotationTag.NAME));
    /**
     * Designator for the value field of an annotation.
     */
    public static final String VALUE_IDENTIFIER = "value";

    /**
     * Constructs a new annotation reader.
     *
     * @param tagName The name associated to the annotation tag.
     */
    private AnnotationTagReader(final String tagName) {
        super(tagName);
    }

    /**
     * Getter for an annotation in a host.
     *
     * @param host The host that may own the annotation.
     * @param name The name associated with the annotation.
     * @return The annotation tag if present in the `host`.
     */
    public Optional<AnnotationTag> getAnnotation(final Host host, final String name) {
        return getAnnotations(host)
                .filter((tag) -> tag.getType().equals(name))
                .findFirst();
    }

    /**
     * Getter for the annotations in a host.
     *
     * @param host The host owning the annotations.
     * @return The annotations associated with the host.
     */
    public Stream<AnnotationTag> getAnnotations(final Host host) {
        return get(host).stream()
                .flatMap((visibilityAnnotationTag) ->
                        visibilityAnnotationTag.getAnnotations().stream()
                                .flatMap(this::getAnnotations));
    }

    /**
     * Getter for all the sub-annotations of a tag.
     *
     * @param annotationTag The starting annotation tag.
     * @return The accumulated sub-annotations.
     */
    public Stream<AnnotationTag> getAnnotations(final AnnotationTag annotationTag) {
        final Stream.Builder<AnnotationTag> builder = Stream.builder();
        builder.add(annotationTag);
        getValue(annotationTag)
                .ifPresent((annotationElement) ->
                        getAnnotations(builder, annotationElement));

        return builder.build();
    }

    /**
     * Get a specific element from an annotation tag.
     *
     * @param annotationTag The annotation tag owning the element.
     * @param identifier    The identifier of the element.
     * @return The annotation element if present in `annotationTag`.
     */
    public Optional<AnnotationElem> getElement(final AnnotationTag annotationTag, final String identifier) {
        return annotationTag.getElems().stream()
                .filter((element) ->
                        element.getName().equals(identifier))
                .findFirst();
    }

    public <T extends AnnotationElem> Optional<T> getElement(final AnnotationTag annotationTag, final String identifier,
                                                             final Class<T> type) {
        return getElement(annotationTag, identifier)
                .map(annotationElem -> {
                    if (annotationElem.getClass() == type) {
                        return type.cast(annotationElem);
                    } else {
                        throw new IllegalStateException(
                                "Unexpected type for element "
                                        + identifier
                                        + " of tag "
                                        + annotationTag
                                        + ": "
                                        + annotationElem.getKind()
                        );
                    }
                });
    }

    /**
     * Getter for the value annotation element.
     *
     * @param annotationTag The tag owning the element.
     * @return The value annotation element if present in `annotationTag`.
     */
    public Optional<AnnotationElem> getValue(final AnnotationTag annotationTag) {
        return getElement(annotationTag, VALUE_IDENTIFIER);
    }

    public <T extends AnnotationElem> Optional<T> getValue(final AnnotationTag annotationTag, final Class<T> type) {
        return getElement(annotationTag, VALUE_IDENTIFIER, type);
    }

    /**
     * Checks whether a host owns a specific annotation.
     *
     * @param host The host that may own the annotation.
     * @param name The name of the annotation
     * @return `true` if the host owns the annotation under `name`, `false` otherwise.
     */
    public boolean hasAnnotation(final Host host, final String name) {
        return getAnnotation(host, name).isPresent();
    }

    public static AnnotationTagReader v() {
        return INSTANCE.get();
    }

    /**
     * Fetches all sub-annotations starting from a single annotation element.
     *
     * @param builder           The accumulator for the sub-annotations.
     * @param annotationElement The annotation element to be traversed for finding the sub-annotations.
     */
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

}
