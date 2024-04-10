package byteback.analysis.global.vimp.tag;

import byteback.analysis.local.vimp.syntax.value.box.ConditionExprBox;
import soot.Value;

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