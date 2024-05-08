package byteback.syntax.scene.type.declaration.member.method.analysis;

import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.Jimple;

import java.util.ArrayList;
import java.util.List;

public class ParameterLocalFinder {

	private static final Lazy<ParameterLocalFinder> INSTANCE = Lazy.from(ParameterLocalFinder::new);

	public static ParameterLocalFinder v() {
		return INSTANCE.get();
	}

	private ParameterLocalFinder() {
	}

	public List<Local> findParameterLocals(final SootMethod sootMethod) {
		final var parameterLocals = new ArrayList<Local>();

		if (sootMethod.hasActiveBody()) {
			final Body body = sootMethod.getActiveBody();

			if (!sootMethod.isStatic()) {
				parameterLocals.add(body.getThisLocal());
			}

			parameterLocals.addAll(body.getParameterLocals());
		} else {
			if (!sootMethod.isStatic()) {
				final SootClass declaringClass = sootMethod.getDeclaringClass();
				final Type declaringType = declaringClass.getType();
				parameterLocals.add(Jimple.v().newLocal("this", declaringType));
			}

			for (int parameterIndex = 0; parameterIndex < sootMethod.getParameterCount(); ++parameterIndex) {
				final Type parameterType = sootMethod.getParameterType(parameterIndex);
				parameterLocals.add(Jimple.v().newLocal("p" + parameterIndex, parameterType));
			}
		}

		return parameterLocals;
	}

}
