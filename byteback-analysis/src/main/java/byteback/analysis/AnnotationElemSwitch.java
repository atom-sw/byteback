package byteback.analysis;

import soot.tagkit.AnnotationElem;

public abstract class AnnotationElemSwitch<R> extends soot.util.annotations.AnnotationElemSwitch
		implements
			Visitor<AnnotationElem, R> {

	@Override
	public void defaultCase(final Object object) {
		caseDefault((AnnotationElem) object);
	}

}
