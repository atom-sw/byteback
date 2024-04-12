package byteback.syntax.member.method.body.tag;

import byteback.syntax.tag.ValuesTag;
import soot.ValueBox;

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
