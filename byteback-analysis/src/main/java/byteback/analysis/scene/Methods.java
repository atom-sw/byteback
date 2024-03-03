package byteback.analysis.scene;

import java.util.ArrayList;
import java.util.List;

import soot.Local;
import soot.SootMethod;
import soot.jimple.internal.JimpleLocal;

public class Methods {

	public static List<Local> makeFakeParameterLocals(final SootMethod method) {
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
