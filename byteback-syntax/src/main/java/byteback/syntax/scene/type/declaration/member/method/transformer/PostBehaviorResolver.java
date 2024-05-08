package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.analysis.ParameterRefFinder;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import soot.*;
import soot.util.NumberedString;

import java.util.ArrayList;
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
		final List<Value> behaviorAguments = ParameterRefFinder.v().findInputRefs(targetMethod).stream()
				.map(Vimp.v()::nest).collect(Collectors.toCollection(ArrayList<Value>::new));
		final Type returnType = targetMethod.getReturnType();

		if (returnType != VoidType.v()) {
			behaviorAguments.add(Vimp.v().nest(Vimp.v().newReturnRef(returnType)));
		}

		return Vimp.v().newCallExpr(behaviorMethod.makeRef(), behaviorAguments);
	}

}
