package byteback.analysis.scene.visitor;

import byteback.analysis.common.visitor.Visitor;
import soot.tag.AbtractAnnotationElementTypeSwitch;
import soot.tag.AnnotationElement;

public abstract class AbtractAnnotationElementSwitch<R> extends AbtractAnnotationElementTypeSwitch<R>
        implements Visitor<AnnotationElement, R> {

    @Override
    public void defaultCase(final Object object) {
        defaultCase((AnnotationElement) object);
    }

}
