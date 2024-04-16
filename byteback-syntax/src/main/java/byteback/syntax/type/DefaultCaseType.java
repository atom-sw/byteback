package byteback.syntax.type;

import soot.Type;
import soot.TypeSwitch;
import soot.util.Switch;
import soot.util.Switchable;

/**
 * A type whose apply method always calls the default method of the given visitor.
 *
 * @author paganma
 */
public interface DefaultCaseType extends Switchable {

    @Override
    default void apply(final Switch visitor) {
        assert this instanceof Type : "Implementer must be of type soot.Type.";

        if (visitor instanceof TypeSwitch<?> typeSwitch) {
            typeSwitch.defaultCase((Type) this);
        }
    }

}
