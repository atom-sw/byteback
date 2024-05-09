package byteback.syntax.scene.type.declaration.member.method.analysis;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.HeapType;
import byteback.syntax.scene.type.declaration.member.method.body.value.HeapRef;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
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

	public Local findHeapLocal(final Body body) {
		for (final Unit unit : body.getUnits()) {
			if (unit instanceof final IdentityUnit identityUnit) {
				if (identityUnit.getRightOp() instanceof HeapRef) {
					return (Local) identityUnit.getLeftOp();
				}
			}
		}

		throw new IllegalArgumentException("Body does not assigns heap local");
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
