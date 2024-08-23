package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import java.util.Collections;
import java.util.Set;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.tag.InferredFramesTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.PureTagMarker;
import byteback.syntax.transformer.TransformationException;
import soot.Body;
import soot.SootMethod;
import soot.ValueBox;
import soot.jimple.ConcreteRef;
import soot.jimple.IdentityRef;

public class FrameConditionValidator extends BodyTransformer {

	private static final Lazy<FrameConditionValidator> INSTANCE = Lazy.from(FrameConditionValidator::new);

	private FrameConditionValidator() {
	}

	public static FrameConditionValidator v() {
		return INSTANCE.get();
	}

	@Override
	public void transformBody(final Body body) {
		final SootMethod sootMethod = body.getMethod();

		if (BehaviorTagMarker.v().hasTag(sootMethod)) {
			return;
		}

		final Set<ConcreteRef> allowedRefs;

		if (PureTagMarker.v().hasTag(sootMethod)) {
			allowedRefs = Collections.emptySet();
		} else {
			allowedRefs = InferredFramesTagAccessor.v().getOrThrow(body).getFrameRefs();
		}

		for (final ValueBox defBox : body.getDefBoxes()) {
			if (defBox.getValue() instanceof final ConcreteRef ref
					&& !(ref instanceof IdentityRef)) {
				if (!allowedRefs.contains(ref)) {
					throw new TransformationException(
							"Unable to assign reference "
									+ ref
									+ " because it is not in the method's frame: " + sootMethod.getSignature(),
							body);
				}
			}
		}
	}

}
