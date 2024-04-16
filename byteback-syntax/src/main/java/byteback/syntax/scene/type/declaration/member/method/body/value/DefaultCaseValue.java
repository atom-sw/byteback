package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.Value;
import soot.jimple.ExprSwitch;
import soot.util.Switch;
import soot.util.Switchable;

/**
 * An expression whose apply method always calls the default method of the given visitor.
 *
 * @author paganma
 */
public interface DefaultCaseValue extends Value, Switchable {

    @Override
    default void apply(final Switch visitor) {
        if (visitor instanceof ExprSwitch exprSwitch) {
            exprSwitch.defaultCase(this);
        }
    }

}
