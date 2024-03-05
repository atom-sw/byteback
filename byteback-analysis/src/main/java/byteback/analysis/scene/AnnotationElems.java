package byteback.analysis.scene;

import byteback.analysis.scene.visitor.AbstractAnnotationElemSwitch;
import byteback.common.function.Lazy;
import soot.tagkit.AnnotationClassElem;
import soot.tagkit.AnnotationStringElem;

import java.util.Optional;

public class AnnotationElems {

	private static final Lazy<AnnotationElems> instance = Lazy.from(AnnotationElems::new);

	public static AnnotationElems v() {
		return instance.get();
	}

	private AnnotationElems() {
	}

	public class StringElemExtractor extends AbstractAnnotationElemSwitch<Optional<String>> {

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

	public class ClassElemExtractor extends AbstractAnnotationElemSwitch<Optional<String>> {

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
