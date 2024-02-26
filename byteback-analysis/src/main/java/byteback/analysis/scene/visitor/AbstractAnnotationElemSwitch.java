package byteback.analysis.model.visitor;

import byteback.analysis.common.visitor.Visitor;
import soot.tagkit.AbstractAnnotationElemTypeSwitch;
import soot.tagkit.AnnotationElem;

public abstract class AbstractAnnotationElemSwitch<R> extends AbstractAnnotationElemTypeSwitch<R>
		implements Visitor<AnnotationElem, R> {

	@Override
	public void defaultCase(final Object object) {
		defaultCase((AnnotationElem) object);
	}

}
