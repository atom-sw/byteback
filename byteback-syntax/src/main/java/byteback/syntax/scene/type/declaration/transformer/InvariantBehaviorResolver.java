package byteback.syntax.scene.type.declaration.transformer;

import java.util.Collections;
import java.util.List;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import byteback.syntax.scene.type.declaration.tag.InvariantMethodsTag;
import byteback.syntax.scene.type.declaration.tag.InvariantMethodsTagAccessor;
import byteback.syntax.transformer.TransformationException;
import soot.*;
import soot.jimple.Jimple;

public class InvariantBehaviorResolver {

	private static final Lazy<InvariantBehaviorResolver> INSTANCE = Lazy.from(InvariantBehaviorResolver::new);

	public static InvariantBehaviorResolver v() {
		return INSTANCE.get();
	}

	public Value makeBehaviorExpr(final SootClass sootClass, final SootMethod behaviorMethod) {
		final List<Value> arguments;

		if (!behaviorMethod.isStatic()) {
			arguments = Collections.singletonList(Vimp.v().nest(Jimple.v().newThisRef(sootClass.getType())));
		} else {
			arguments = Collections.emptyList();
		}

		return Vimp.v().newCallExpr(behaviorMethod.makeRef(), arguments);
	}

	public Value resolveBehavior(final SootClass sootClass, final String behaviorName) {
		final SootMethod behaviorMethod = sootClass.getMethodUnsafe(behaviorName, Collections.emptyList(), BooleanType.v());

		if (behaviorMethod != null) {
			InvariantMethodsTagAccessor.v()
				.putIfAbsent(sootClass, InvariantMethodsTag::new)
				.addInvariantMethod(behaviorMethod);

			if (!BehaviorTagMarker.v().hasTag(behaviorMethod)) {
				throw new TransformationException(
						"Not a behavior method: "
								+ behaviorName);
			}
		} else {
			throw new TransformationException(
					"Could not find behavior method: "
							+ behaviorName);
		}

		return makeBehaviorExpr(sootClass, behaviorMethod);
	}

}
