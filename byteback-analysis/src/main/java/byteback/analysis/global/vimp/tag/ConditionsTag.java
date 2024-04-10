package byteback.analysis.global.vimp.tag;

import byteback.analysis.local.vimp.syntax.value.box.ConditionExprBox;

import java.util.List;

/**
 * specific for conditional expressions and use it to parametrize the Tag.
 */
public abstract class ConditionsTag extends ValuesTag<ConditionExprBox> {

    public ConditionsTag(final List<ConditionExprBox> conditionBoxes) {
        super(conditionBoxes);
    }

}
