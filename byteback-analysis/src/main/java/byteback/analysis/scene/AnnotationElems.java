package byteback.analysis.scene;

import byteback.analysis.scene.visitor.AbstractAnnotationElemTypeSwitch;
import byteback.common.function.Lazy;
import soot.tagkit.AnnotationClassElem;
import soot.tagkit.AnnotationStringElem;

import java.util.Optional;

/**
 * Utility classes to extract Soot annotation elements.
 *
 * @author paganma
 */
public class AnnotationElems {

	private static final Lazy<AnnotationElems> instance = Lazy.from(AnnotationElems::new);

	public static AnnotationElems v() {
		return instance.get();
	}

	private AnnotationElems() {
	}

	public static class StringElemExtractor extends AbstractAnnotationElemTypeSwitch<Optional<String>> {

		public String value;

		@Override
		public void caseAnnotationStringElem(final AnnotationStringElem element) {
			this.value = element.getValue();
		}

		@Override
		public Optional<String> getResult() {
			return Optional.ofNullable(value);
		}

	}

	public static class ClassElemExtractor extends AbstractAnnotationElemTypeSwitch<Optional<String>> {

		public String value;

		@Override
		public void caseAnnotationClassElem(final AnnotationClassElem element) {
			this.value = element.getDesc();
		}

		@Override
		public Optional<String> getResult() {
			return Optional.ofNullable(value);
		}

	}

}
