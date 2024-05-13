package byteback.syntax.scene.type.declaration.member.method.body.value.analyzer;

import byteback.common.function.Lazy;
import soot.Type;
import soot.Value;

/**
 * Default type interpreter yielding the Jimple-type of a given value.
 *
 * @author paganma
 */
public class JimpleTypeInterpreter implements TypeInterpreter<Value> {

	private static final Lazy<JimpleTypeInterpreter> INSTANCE = Lazy.from(JimpleTypeInterpreter::new);

	public static JimpleTypeInterpreter v() {
		return INSTANCE.get();
	}

	private JimpleTypeInterpreter() {
	}

	@Override
	public Type typeOf(final Value value) {
		// The Jimple type corresponds to whatever the `getType` implementation of the
		// `value` returns.
		return value.getType();
	}

}
