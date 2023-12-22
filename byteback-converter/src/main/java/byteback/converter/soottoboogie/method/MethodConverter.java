package byteback.converter.soottoboogie.method;

import java.util.Iterator;
import soot.SootMethod;
import soot.Type;

public abstract class MethodConverter {

	public static String printType(final Type type) {
		return type.toString().replace("[", "").replace("]", "?");
	}

	public static String methodName(final SootMethod method) {
		final var builder = new StringBuilder();
		final Iterator<Type> typeIterator = method.getParameterTypes().iterator();
		final String methodName = method.getName().replace(" ", "$");
		builder.append(method.getDeclaringClass().getName());
		builder.append(".");
		builder.append(methodName.replace("<", "$").replace(">", "$"));
		builder.append("#");

		builder.append(printType(method.getReturnType()));

		builder.append("#");

		while (typeIterator.hasNext()) {
			builder.append(printType(typeIterator.next()));
			builder.append("#");
		}

		builder.append("#");

		return builder.toString();
	}

}
