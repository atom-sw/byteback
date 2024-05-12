package byteback.syntax.scene.type.declaration.member.method.analysis;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.HeapRef;
import byteback.syntax.scene.type.declaration.member.method.body.value.OldHeapRef;
import byteback.syntax.scene.type.declaration.member.method.body.value.ReturnRef;
import byteback.syntax.scene.type.declaration.member.method.body.value.ThrownRef;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.ExceptionalTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.OperatorTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.TwoStateTagMarker;
import soot.*;
import soot.jimple.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParameterRefFinder {

	private static final Lazy<ParameterRefFinder> INSTANCE = Lazy.from(ParameterRefFinder::new);

	public static ParameterRefFinder v() {
		return INSTANCE.get();
	}

	private ParameterRefFinder() {
	}

	public List<IdentityRef> findInputRefs(final SootMethod sootMethod) {
		final var inputRefs = new ArrayList<IdentityRef>();

		if (BehaviorTagMarker.v().hasTag(sootMethod)) {
			if (!OperatorTagMarker.v().hasTag(sootMethod)) {
				final HeapRef heapRef = Vimp.v().newHeapRef();
				inputRefs.add(heapRef);
			}

			if (TwoStateTagMarker.v().hasTag(sootMethod)) {
				final OldHeapRef oldHeapRef = Vimp.v().newOldHeapRef();
				inputRefs.add(oldHeapRef);
			}

			if (ExceptionalTagMarker.v().hasTag(sootMethod)) {
				final ThrownRef thrownRef = Vimp.v().newThrownRef();
				inputRefs.add(thrownRef);
			}
		}

		if (!sootMethod.isStatic()) {
			final SootClass declaringClass = sootMethod.getDeclaringClass();
			final RefType thisType = declaringClass.getType();
			final ThisRef thisRef = Jimple.v().newThisRef(thisType);
			inputRefs.add(thisRef);
		}

		for (int parameterIndex = 0; parameterIndex < sootMethod.getParameterCount(); ++parameterIndex) {
			final Type argumentType = sootMethod.getParameterType(parameterIndex);
			final ParameterRef argumentLocal = Jimple.v().newParameterRef(argumentType, parameterIndex);
			inputRefs.add(argumentLocal);
		}

		return Collections.unmodifiableList(inputRefs);
	}

	public List<ConcreteRef> findOutputRefs(final SootMethod sootMethod) {
		final var outputRefs = new ArrayList<ConcreteRef>();
		final Type returnType = sootMethod.getReturnType();

		if (returnType != VoidType.v()) {
			final ReturnRef returnRef = Vimp.v().newReturnRef(returnType);
			outputRefs.add(returnRef);
		}

		if (!BehaviorTagMarker.v().hasTag(sootMethod)) {
			final ThrownRef thrownRef = Vimp.v().newThrownRef();
			outputRefs.add(thrownRef);
		}

		return Collections.unmodifiableList(outputRefs);
	}

}
