package byteback.analysis.local.vimp.tag.body;

import byteback.analysis.global.vimp.tag.ValuesTag;
import soot.ValueBox;
import soot.jimple.ConcreteRef;

import java.util.List;

public class InferredLocalFramesTag extends ValuesTag<ValueBox> {

    public static final String NAME = "InferredFramesTag";

    public InferredLocalFramesTag(final List<ValueBox> refBoxes) {
        super(refBoxes);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
