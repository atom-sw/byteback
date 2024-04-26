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

    final List<Value> conditions;

    public ConditionsTag(final List<Value> conditions) {
        this.conditions = conditions;
    }

    public List<Value> getConditions() {
        return conditions;
    }

    public void addCondition(final Value condition) {
        conditions.add(condition);
    }

    @Override
    public byte[] getValue() {
        return new byte[0];
    }

}
