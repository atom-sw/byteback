package byteback.syntax.member.method.tag;

import byteback.syntax.value.box.ConditionExprBox;

import java.util.List;

public class PostconditionsTag extends ConditionsTag {

    public static final String NAME = "PostconditionsTag";

    public PostconditionsTag(final List<ConditionExprBox> conditionBoxes) {
        super(conditionBoxes);
    }

    @Override
    public String getName() {
        return NAME;
    }

}