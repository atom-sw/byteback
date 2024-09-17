package byteback.syntax.scene.type.declaration.member.method.transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.analysis.ParameterRefFinder;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.HeapRef;
import byteback.syntax.scene.type.declaration.member.method.tag.ImplicitTagMarker;
import soot.*;
import soot.jimple.ThisRef;
import soot.util.NumberedString;

public class PreBehaviorResolver extends BehaviorResolver {

	private static final Lazy<PreBehaviorResolver> INSTANCE = Lazy.from(PreBehaviorResolver::new);

	public static PreBehaviorResolver v() {
		return INSTANCE.get();
	}

	@Override
	protected NumberedString makeBehaviorSignature(final SootMethod targetMethod, final String behaviorName) {
		final List<Type> parameterTypes = targetMethod.getParameterTypes();
		final Type returnType = BooleanType.v();
		final var behaviorSignature = new MethodSubSignature(behaviorName, returnType, parameterTypes);

		return behaviorSignature.numberedSubSig;
	}

	@Override
	protected Value makeBehaviorExpr(final SootMethod targetMethod, final SootMethod behaviorMethod) {
		final List<Value> behaviorArguments;

		if (!ImplicitTagMarker.v().hasTag(behaviorMethod)) {
			behaviorArguments = ParameterRefFinder.v().findInputRefs(targetMethod).stream()
				.map(Vimp.v()::nest).collect(Collectors.toCollection(ArrayList<Value>::new));
		} else {
			behaviorArguments = ParameterRefFinder.v().findInputRefs(targetMethod).stream()
				.filter((inputRef) -> inputRef instanceof HeapRef || inputRef instanceof ThisRef)
				.map(Vimp.v()::nest).collect(Collectors.toCollection(ArrayList<Value>::new));
		}

		return Vimp.v().newCallExpr(behaviorMethod.makeRef(), behaviorArguments);
	}

}
