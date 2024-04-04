package byteback.analysis.local.vimp.tag.body;

import soot.jimple.ConcreteRef;

import java.util.List;

public class InferredLocalFramesTag extends ValuesTag<ConcreteRef> {

    public static final String NAME = "InferredFramesTag";

    public InferredLocalFramesTag(final List<ConcreteRef> values) {
        super(values);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
