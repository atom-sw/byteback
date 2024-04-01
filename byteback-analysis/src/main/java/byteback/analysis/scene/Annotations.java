package byteback.analysis.scene;

import java.util.Optional;
import java.util.stream.Stream;

import byteback.common.function.Lazy;
import soot.tagkit.AnnotationAnnotationElem;
import soot.tagkit.AnnotationArrayElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;

/**
 * TODO: Maybe we can move this to VisibilityAnnotationReader?
 * Utility class to work with Soot annotations.
 *
 * @author paganma
 */
public class Annotations {

	private static final Lazy<Annotations> instance = Lazy.from(Annotations::new);

	public static Annotations v() {
		return instance.get();
	}

	private Annotations() {
	}

	public static final String VALUE_IDENTIFIER = "value";

	// TODO: Not sure if this is needed.
	public Optional<AnnotationElem> getValue(final AnnotationTag annotationTag) {
		return getElem(annotationTag, VALUE_IDENTIFIER);
	}

	public Optional<AnnotationElem> getElem(final AnnotationTag annotationTag, final String identifier) {
		return annotationTag.getElems().stream().filter((elem) -> elem.getName().equals(identifier)).findFirst();
	}

	private void getAnnotations(final Stream.Builder<AnnotationTag> builder, final AnnotationElem annotationElement) {
		if (annotationElement instanceof AnnotationArrayElem annotationArrayElement) {
			for (AnnotationElem value : annotationArrayElement.getValues()) {
				getAnnotations(builder, value);
			}
		} else if (annotationElement instanceof AnnotationAnnotationElem annotationAnnotationElement) {
			final AnnotationTag tag = annotationAnnotationElement.getValue();
			builder.accept(tag);
			getValue(tag).ifPresent((elem) -> getAnnotations(builder, elem));
		}
	}

	/**
	 * Given an annotation tag, it returns all the sub-annotations in depth-first pre-order.
	 * @param annotationTag The annotation tag to scan.
	 * @return The sub-annotation tags.
	 */
	public Stream<AnnotationTag> getAnnotations(final AnnotationTag annotationTag) {
		final Stream.Builder<AnnotationTag> builder = Stream.builder();
		builder.add(annotationTag);
		getValue(annotationTag).ifPresent((elem) -> {
			getAnnotations(builder, elem);
		});

		return builder.build();
	}

}
