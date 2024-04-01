package byteback.analysis.body.jimple.syntax;

import byteback.analysis.body.common.syntax.TypeInterpreter;
import byteback.common.function.Lazy;
import soot.Type;
import soot.Value;

/**
 * Default type interpreter yielding the Jimple-type of a given value.
 *
 * @author paganma
 */
public class JimpleTypeInterpreter implements TypeInterpreter<Value> {

    private static final Lazy<JimpleTypeInterpreter> instance = Lazy.from(JimpleTypeInterpreter::new);

    public static JimpleTypeInterpreter v() {
        return instance.get();
    }

    private JimpleTypeInterpreter() {
    }

    @Override
    public Type typeOf(final Value value) {
        // The Jimple type corresponds to whatever the `getType` implementation of the `value` returns.
        return value.getType();
    }

}
