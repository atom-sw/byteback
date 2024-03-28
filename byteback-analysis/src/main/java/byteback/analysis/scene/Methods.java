package byteback.analysis.scene;

import java.util.ArrayList;
import java.util.List;

import byteback.common.function.Lazy;
import soot.Local;
import soot.SootMethod;
import soot.jimple.internal.JimpleLocal;

/**
 * Utility class to work with Soot methods.
 *
 * @author paganma
 */
public class Methods {

	private static final Lazy<Methods> instance = Lazy.from(Methods::new);

	public static Methods v() {
		return instance.get();
	}

	private Methods() {
	}

	public List<Local> makeFakeParameterLocals(final SootMethod method) {
		final List<Local> parameterLocals = new ArrayList<>();

		if (!method.isStatic()) {
			parameterLocals.add(new JimpleLocal("this", method.getDeclaringClass().getType()));
		}

		for (int i = 0; i < method.getParameterCount(); ++i) {
			final String name = "p" + i;
			parameterLocals.add(new JimpleLocal(name, method.getParameterType(i)));
		}

		return parameterLocals;
	}

}
