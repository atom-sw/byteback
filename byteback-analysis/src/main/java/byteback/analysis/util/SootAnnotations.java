package byteback.analysis.util;

import byteback.analysis.AnnotationElemSwitch;
import java.util.Optional;
import java.util.stream.Stream;
import soot.tagkit.AnnotationAnnotationElem;
import soot.tagkit.AnnotationArrayElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;

public class SootAnnotations {

	private static String VALUE_IDENTIFIER = "value";

	public static Optional<AnnotationElem> getValue(final AnnotationTag tag) {
		return getElem(tag, VALUE_IDENTIFIER);
	}

	public static Optional<AnnotationElem> getElem(final AnnotationTag tag, final String identifier) {
		return tag.getElems().stream().filter((elem) -> elem.getName().equals(identifier)).findFirst();
	}

	private static void getAnnotations(final Stream.Builder<AnnotationTag> builder, final AnnotationElem element) {
		element.apply(new AnnotationElemSwitch<>() {

			@Override
			public void caseAnnotationArrayElem(final AnnotationArrayElem element) {
				for (AnnotationElem value : element.getValues()) {
					getAnnotations(builder, value);
				}
			}

			@Override
			public void caseAnnotationAnnotationElem(final AnnotationAnnotationElem element) {
				final AnnotationTag tag = element.getValue();
				builder.accept(tag);
				getValue(tag).ifPresent((elem) -> getAnnotations(builder, elem));
			}

		});
	}

	public static Stream<AnnotationTag> getAnnotations(final AnnotationTag tag) {
		Stream.Builder<AnnotationTag> builder = Stream.builder();
		builder.add(tag);
		getValue(tag).ifPresent((elem) -> {
			getAnnotations(builder, elem);
		});

		return builder.build();
	}

}
