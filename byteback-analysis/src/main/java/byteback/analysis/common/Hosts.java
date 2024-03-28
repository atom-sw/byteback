package byteback.analysis.common;

import java.util.Optional;
import java.util.stream.Stream;

import byteback.analysis.common.tag.VisibilityAnnotationReader;
import byteback.analysis.scene.Annotations;
import byteback.common.function.Lazy;
import soot.tagkit.*;

/**
 * Utility functions to work with Soot tags.
 *
 * @author paganma
 */
public class Hosts {

	private static final Lazy<Hosts> instance = Lazy.from(Hosts::new);

	public static Hosts v() {
		return instance.get();
	}

	private Hosts() {
	}

	public Stream<AnnotationTag> getAnnotations(final Host host) {
		return VisibilityAnnotationReader.v().get(host).stream()
				.flatMap((visibilityAnnotationTag) -> visibilityAnnotationTag.getAnnotations().stream());
	}

	public Optional<AnnotationTag> getAnnotation(final Host host, final String name) {
		return getAnnotations(host)
				.filter((tag) -> tag.getType().equals(name))
				.findFirst();
	}

	public Optional<AnnotationElem> getAnnotationValue(final Host host, final String name) {
		return getAnnotation(host, name)
				.flatMap(Annotations.v()::getValue);
	}

	public boolean hasAnnotation(final Host host, final String name) {
		return getAnnotation(host, name)
				.isPresent();
	}

}
