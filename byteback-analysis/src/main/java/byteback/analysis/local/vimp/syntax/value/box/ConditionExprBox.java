package byteback.analysis.local.vimp.syntax.value.box;

import byteback.analysis.local.vimp.analyzer.value.VimpTypeInterpreter;
import soot.BooleanType;
import soot.Value;

/**
 * A condition box for holding a boolean conditional value.
 * Unlike Jimple, a Vimp's conditional expression may be exclusively of type boolean.
 *
 * @author paganma
 */
public class ConditionExprBox extends soot.jimple.internal.ConditionExprBox {

    public ConditionExprBox(Value value) {
        super(value);
    }

    @Override
    public boolean canContainValue(final Value value) {
        return VimpTypeInterpreter.v().typeOf(value) == BooleanType.v();
    }

}