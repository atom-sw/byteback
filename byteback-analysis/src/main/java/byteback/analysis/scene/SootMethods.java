package byteback.analysis.scene;

import byteback.analysis.common.namespace.BBLibNamespace;
import java.util.ArrayList;
import java.util.List;

import soot.Local;
import soot.SootMethod;
import soot.jimple.internal.JimpleLocal;

public class SootMethods {

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

	public static boolean hasBody(final SootMethod method) {
		return method.isConcrete() && !method.isPhantom() && !method.getDeclaringClass().isPhantom()
				&& (!SootClasses.isBasicClass(method.getDeclaringClass()) || BBLibNamespace.isPredicateMethod(method)
						|| BBLibNamespace.isPureMethod(method));
	}

}
