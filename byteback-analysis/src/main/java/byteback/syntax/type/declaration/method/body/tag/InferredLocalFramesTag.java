package byteback.syntax.type.declaration.method.body.tag;

import byteback.syntax.tag.ValuesTag;
import soot.jimple.ConcreteRef;

import java.util.List;

public class InferredLocalFramesTag extends ValuesTag<ConcreteRef> {

    public static final String NAME = "InferredFramesTag";

    public InferredLocalFramesTag(final List<ConcreteRef> refs) {
        super(refs);
    }

    public InferredLocalFramesTag() {
    }

    @Override
    public String getName() {
        return NAME;
    }

}
