package byteback.syntax.scene.type.declaration.member.method.body.value.box;

import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpTypeInterpreter;
import soot.AbstractValueBox;
import soot.BooleanType;
import soot.Value;
import soot.ValueBox;

/**
 * A condition box for holding a boolean conditional value.
 * Unlike Jimple, a Vimp's conditional expression may be exclusively of type boolean.
 *
 * @author paganma
 */
public class ConditionExprBox extends AbstractValueBox {

    public ConditionExprBox(final Value value) {
        setValue(value);
    }

    @Override
    public boolean canContainValue(final Value value) {
        return VimpTypeInterpreter.v().typeOf(value) == BooleanType.v();
    }

}
