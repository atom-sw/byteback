package byteback.analysis.scene;

import byteback.analysis.scene.visitor.AbstractAnnotationElemSwitch;
import java.util.Optional;
import java.util.stream.Stream;

import byteback.common.function.Lazy;
import soot.tagkit.AnnotationAnnotationElem;
import soot.tagkit.AnnotationArrayElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;

public class Annotations {

	private static final Lazy<Annotations> instance = Lazy.from(Annotations::new);

	public static Annotations v() {
		return instance.get();
	}

	private Annotations() {
	}

	private static final String VALUE_IDENTIFIER = "value";

	public Optional<AnnotationElem> getValue(final AnnotationTag tag) {
		return getElem(tag, VALUE_IDENTIFIER);
	}

	public Optional<AnnotationElem> getElem(final AnnotationTag tag, final String identifier) {
		return tag.getElems().stream().filter((elem) -> elem.getName().equals(identifier)).findFirst();
	}

	private void getAnnotations(final Stream.Builder<AnnotationTag> builder, final AnnotationElem element) {
		element.apply(new AbstractAnnotationElemSwitch<>() {

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

	public Stream<AnnotationTag> getAnnotations(final AnnotationTag tag) {
		Stream.Builder<AnnotationTag> builder = Stream.builder();
		builder.add(tag);
		getValue(tag).ifPresent((elem) -> {
			getAnnotations(builder, elem);
		});

		return builder.build();
	}

}
