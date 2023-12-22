package byteback.analysis.util;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;

public class SootClasses {

	public static boolean isBasicClass(final SootClass clazz) {
		final String name = clazz.getName();

		return clazz.isJavaLibraryClass() || name.startsWith("jdk") || name.startsWith("scala")
				|| name.startsWith("groovy") || name.startsWith("sun") || name.startsWith("java")
				|| name.startsWith("soot") || name.startsWith("org.codehaus") || name.startsWith("kotlin");
	}

	public static Optional<SootMethod> lookupMethodByPrefix(final SootClass clazz, final String name,
			final List<Type> parameterTypes, final Type returnType) {
		METHOD_LOOKUP : for (final SootMethod method : clazz.getMethods()) {
			if (!method.getName().equals(name)) {
				continue;
			}

			if (!method.getReturnType().equals(returnType)) {
				continue;
			}

			final Iterator<Type> parameterTypeIt = parameterTypes.iterator();

			for (final Type type : method.getParameterTypes()) {
				if (!parameterTypeIt.next().equals(type)) {
					continue METHOD_LOOKUP;
				}
			}

			return Optional.of(method);
		}

		return Optional.empty();
	}

}
