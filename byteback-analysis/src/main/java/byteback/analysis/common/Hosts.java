package byteback.analysis.common;

import java.util.Optional;
import java.util.stream.Stream;

import byteback.analysis.scene.Annotations;
import soot.tagkit.*;

public class Hosts {

	public static Optional<Tag> getTag(final Host host, final String name) {
		return Optional.ofNullable(host.getTag(name));
	}

	public static boolean hasTag(final Host host, final String name) {
		return getTag(host, name)
				.isPresent();
	}

	public static Stream<AnnotationTag> getAnnotations(final Host host) {
		if (getTag(host, "VisibilityAnnotationTag").orElse(null)
				instanceof final VisibilityAnnotationTag tag) {
			return tag.getAnnotations().stream();
		} else {
			return Stream.empty();
		}
	}

	public static Optional<AnnotationTag> getAnnotation(final Host host, final String name) {
		return getAnnotations(host)
				.filter((tag) -> tag.getType().equals(name))
				.findFirst();
	}

	public Optional<AnnotationElem> getAnnotationValue(final Host host, final String name) {
		return getAnnotation(host, name)
				.flatMap(Annotations::getValue);
	}

	public static boolean hasAnnotation(final Host host, final String name) {
		return getAnnotation(host, name)
				.isPresent();
	}

}
