package byteback.analysis.common;

import java.util.Optional;
import java.util.stream.Stream;

import byteback.analysis.common.tag.LocationTag;
import byteback.analysis.scene.Annotations;
import byteback.common.function.Lazy;
import soot.tagkit.*;

/**
 * Utility functions to work with Soot tags.
 * @author paganma
 */
public class Hosts {

	private static final Lazy<Hosts> instance = Lazy.from(Hosts::new);

	public static Hosts v() {
		return instance.get();
	}

	private Hosts() {
	}

	public Optional<Tag> getTag(final Host host, final String name) {
		return Optional.ofNullable(host.getTag(name));
	}

	public boolean hasTag(final Host host, final String name) {
		return getTag(host, name)
				.isPresent();
	}

	public Stream<AnnotationTag> getAnnotations(final Host host) {
		if (getTag(host, "VisibilityAnnotationTag").orElse(null)
				instanceof final VisibilityAnnotationTag tag) {
			return tag.getAnnotations().stream();
		} else {
			return Stream.empty();
		}
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
