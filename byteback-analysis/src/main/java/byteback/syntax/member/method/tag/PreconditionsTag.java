package byteback.syntax.member.method.tag;

import byteback.syntax.value.box.ConditionExprBox;

import java.util.List;

public class PreconditionsTag extends ConditionsTag {

    public static final String NAME = "PreconditionsTag";

    public PreconditionsTag(final List<ConditionExprBox> conditionBoxes) {
        super(conditionBoxes);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
