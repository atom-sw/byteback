package byteback.syntax.member.method.tag;

import byteback.syntax.tag.ValuesTag;
import byteback.syntax.value.box.ConditionExprBox;

import java.util.List;

/**
 * specific for conditional expressions and use it to parametrize the Tag.
 */
public abstract class ConditionsTag extends ValuesTag<ConditionExprBox> {

    public ConditionsTag(final List<ConditionExprBox> conditionBoxes) {
        super(conditionBoxes);
    }

}
