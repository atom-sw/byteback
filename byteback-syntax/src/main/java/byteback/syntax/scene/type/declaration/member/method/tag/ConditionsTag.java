package byteback.syntax.scene.type.declaration.member.method.tag;

import soot.Value;
import soot.tagkit.Tag;

import java.util.List;

/**
 * specific for conditional expressions and use it to parametrize the Tag.
 *
 * @author paganma
 */
public abstract class ConditionsTag implements Tag {

    private List<Value> conditions;

    public ConditionsTag(final List<Value> conditions) {
        this.conditions = conditions;
    }

    public List<Value> getConditions() {
        return conditions;
    }

    public void setConditions(final List<Value> conditions) {
        this.conditions = conditions;
    }

    @Override
    public byte[] getValue() {
        return new byte[0];
    }

}
