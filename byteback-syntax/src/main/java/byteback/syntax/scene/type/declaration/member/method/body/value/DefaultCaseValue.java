package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.Value;
import soot.jimple.ExprSwitch;
import soot.util.Switch;

/**
 * An expression whose apply method always calls the default method of the given
 * visitor.
 *
 * @author paganma
 */
public interface DefaultCaseValue extends Value {

	@Override
	default void apply(final Switch visitor) {
		if (visitor instanceof final ExprSwitch exprSwitch) {
			exprSwitch.defaultCase(this);
		}
	}

}
