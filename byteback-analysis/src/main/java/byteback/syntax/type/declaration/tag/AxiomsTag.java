package byteback.syntax.type.declaration.tag;

import byteback.syntax.tag.ValuesTag;
import byteback.syntax.value.box.ConditionExprBox;

import java.util.List;

public class AxiomsTag extends ValuesTag<ConditionExprBox> {

    public static String NAME = "AxiomsTag";

    public AxiomsTag(final List<ConditionExprBox> valueBoxes) {
        super(valueBoxes);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
