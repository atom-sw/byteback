package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import byteback.syntax.scene.type.declaration.member.method.body.tag.InferredFramesTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.body.tag.InferredFramesTag;
import soot.Body;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ArrayRef;
import soot.jimple.ConcreteRef;
import soot.jimple.FieldRef;

import java.util.HashSet;

public class FrameConditionFinder extends BodyTransformer {

	private final static Lazy<FrameConditionFinder> INSTANCE = Lazy.from(FrameConditionFinder::new);

	public static FrameConditionFinder v() {
		return INSTANCE.get();
	}

	private FrameConditionFinder() {
	}

	@Override
	public void transformBody(final Body body) {
		if (BehaviorTagMarker.v().hasTag(body.getMethod())) {
			return;
		}

		final var inferredFrames = new HashSet<ConcreteRef>();

		for (final ValueBox valueBox : body.getDefBoxes()) {
			final Value value = valueBox.getValue();

			if (value instanceof final ConcreteRef concreteRef &&
					(concreteRef instanceof ArrayRef || concreteRef instanceof FieldRef)) {
				inferredFrames.add(concreteRef);
			}
		}

		final var inferredFramesTag = new InferredFramesTag(inferredFrames);
		InferredFramesTagAccessor.v().put(body, inferredFramesTag);
	}

}
