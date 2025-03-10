package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.analysis.ParameterRefFinder;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.HeapRef;
import byteback.syntax.scene.type.declaration.member.method.body.value.ThrownRef;
import byteback.syntax.scene.type.declaration.member.method.tag.ImplicitTagMarker;
import soot.*;
import soot.jimple.ThisRef;
import soot.util.NumberedString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PostBehaviorResolver extends BehaviorResolver {

	private static final Lazy<PostBehaviorResolver> INSTANCE = Lazy.from(PostBehaviorResolver::new);

	public static PostBehaviorResolver v() {
		return INSTANCE.get();
	}

	@Override
	protected NumberedString makeBehaviorSignature(final SootMethod targetMethod, final String behaviorName) {
		final var behaviorParameterTypes = new ArrayList<>(targetMethod.getParameterTypes());

		if (targetMethod.getReturnType() != VoidType.v()) {
			behaviorParameterTypes.add(targetMethod.getReturnType());
		}

		final var behaviorSignature = new MethodSubSignature(behaviorName, BooleanType.v(), behaviorParameterTypes);

		return behaviorSignature.numberedSubSig;
	}

	@Override
	protected Value makeBehaviorExpr(final SootMethod targetMethod, final SootMethod behaviorMethod) {
		final List<Value> behaviorArguments;

		if (!ImplicitTagMarker.v().hasTag(behaviorMethod)) {
			behaviorArguments = ParameterRefFinder.v().findInputRefs(targetMethod).stream()
			.map(Vimp.v()::nest).collect(Collectors.toCollection(ArrayList<Value>::new));
			final Type returnType = targetMethod.getReturnType();

			if (returnType != VoidType.v()) {
				behaviorArguments.add(Vimp.v().nest(Vimp.v().newReturnRef(returnType)));
			}
		} else {
			behaviorArguments = ParameterRefFinder.v().findInputRefs(targetMethod).stream()
				.filter((inputRef) -> inputRef instanceof HeapRef || inputRef instanceof ThisRef || inputRef instanceof ThrownRef)
				.map(Vimp.v()::nest)
				.collect(Collectors.toCollection(ArrayList<Value>::new));
		}

		return Vimp.v().newCallExpr(behaviorMethod.makeRef(), behaviorArguments);
	}

}
