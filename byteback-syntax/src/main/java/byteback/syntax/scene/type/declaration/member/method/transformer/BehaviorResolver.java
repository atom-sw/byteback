package byteback.syntax.scene.type.declaration.member.method.transformer;

import java.util.Collections;

import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.ImplicitTagMarker;
import byteback.syntax.transformer.TransformationException;
import soot.*;
import soot.util.NumberedString;

/**
 * @author paganma
 */
public abstract class BehaviorResolver {

	protected abstract NumberedString makeBehaviorSignature(final SootMethod targetMethod, final String behaviorName);

	protected abstract Value makeBehaviorExpr(final SootMethod targetMethod, final SootMethod behaviorMethod);

	protected SootMethod lookupBehavior(final SootMethod targetMethod, final String behaviorName) {
		final SootClass declaringClass = targetMethod.getDeclaringClass();
		final NumberedString behaviorSignature = makeBehaviorSignature(targetMethod, behaviorName);
		final SootMethod behaviorMethod = declaringClass.getMethodUnsafe(behaviorSignature);

		if (behaviorMethod != null) {
			if (!BehaviorTagMarker.v().hasTag(behaviorMethod)) {
				throw new TransformationException(
						"Not a behavior method: "
								+ behaviorName,
						targetMethod);
			}

			return behaviorMethod;
		} else {
			final SootMethod implicitBehaviorMethod = declaringClass.getMethod(behaviorName, Collections.emptyList());

			if (implicitBehaviorMethod != null && ImplicitTagMarker.v().hasTag(implicitBehaviorMethod)) {
				return implicitBehaviorMethod;
			}

			throw new TransformationException(
					"Could not find behavior method: "
							+ behaviorName,
					targetMethod);
		}
	}

	public final Value resolveBehavior(final SootMethod targetMethod, final String behaviorName) {
		final SootMethod behaviorMethod = lookupBehavior(targetMethod, behaviorName);

		return makeBehaviorExpr(targetMethod, behaviorMethod);
	}

}
