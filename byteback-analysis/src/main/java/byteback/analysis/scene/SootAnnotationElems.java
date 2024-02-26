package byteback.analysis.model;

import byteback.analysis.model.visitor.AbstractAnnotationElemSwitch;
import soot.tagkit.AnnotationClassElem;
import soot.tagkit.AnnotationStringElem;

import java.util.Optional;

public class SootAnnotationElems {

	public static class StringElemExtractor extends AbstractAnnotationElemSwitch<Optional<String>> {

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

	public static class ClassElemExtractor extends AbstractAnnotationElemSwitch<Optional<String>> {

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
