package byteback.analysis.scene.visitor;

import byteback.analysis.common.visitor.Visitor;
import soot.tagkit.AnnotationElem;

/**
 * Visitor extension of AbstractAnnotationElemTypeSwitch.
 * @see soot.tagkit.AbstractAnnotationElemTypeSwitch
 * @param <R> The type of the return value of this visitor.
 */
public abstract class AbstractAnnotationElemTypeSwitch<R> extends soot.tagkit.AbstractAnnotationElemTypeSwitch<R>
		implements Visitor<AnnotationElem, R> {

	@Override
	public void defaultCase(final Object object) {
		defaultCase((AnnotationElem) object);
	}

}
