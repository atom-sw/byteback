package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.syntax.tag.ValuesTag;
import soot.Value;

import java.util.List;

/**
 * specific for conditional expressions and use it to parametrize the Tag.
 */
public abstract class ConditionsTag extends ValuesTag<Value> {

    public ConditionsTag(final List<Value> values) {
        super(values);
    }

}
