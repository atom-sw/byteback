package byteback.syntax.scene.type.declaration.member.method.transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.analysis.ParameterRefFinder;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import soot.*;
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
		final List<Value> behaviorAguments = ParameterRefFinder.v().findInputRefs(targetMethod).stream()
				.map(Vimp.v()::nest).collect(Collectors.toCollection(ArrayList<Value>::new));

		return Vimp.v().newCallExpr(behaviorMethod.makeRef(), behaviorAguments);
	}

}
