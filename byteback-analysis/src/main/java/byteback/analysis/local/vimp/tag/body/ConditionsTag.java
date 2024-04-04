package byteback.analysis.local.vimp.tag.body;

import soot.Value;

import java.util.List;

/**
 * TODO: Conditional tags should only contain conditional expressions. Hence we should consider creating a new Box type
 * specific for conditional expressions and use it to parametrize the Tag.
 */
public abstract class ConditionsTag extends ValuesTag<Value> {

    public ConditionsTag(final List<Value> values) {
        super(values);
    }

}
