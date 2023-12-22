package byteback.analysis.util;

import java.util.Optional;
import java.util.stream.Stream;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;
import soot.tagkit.Host;
import soot.tagkit.VisibilityAnnotationTag;

public class SootHosts {

	public static Stream<AnnotationTag> getAnnotations(final Host host) {
		final var tag = (VisibilityAnnotationTag) host.getTag("VisibilityAnnotationTag");

		if (tag != null) {
			return tag.getAnnotations().stream();
		} else {
			return Stream.empty();
		}
	}

	public static Optional<AnnotationTag> getAnnotation(final Host host, final String name) {
		return getAnnotations(host).filter((tag) -> tag.getType().equals(name)).findFirst();
	}

	public Optional<AnnotationElem> getAnnotationValue(final Host host, final String name) {
		return getAnnotation(host, name).flatMap(SootAnnotations::getValue);
	}

	public static boolean hasAnnotation(final Host host, final String name) {
		return getAnnotation(host, name).isPresent();
	}

}
