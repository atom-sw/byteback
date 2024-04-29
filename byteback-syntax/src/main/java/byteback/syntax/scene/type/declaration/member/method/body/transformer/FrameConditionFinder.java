package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import byteback.syntax.scene.type.declaration.member.method.body.tag.InferredFramesTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.body.tag.InferredFramesTag;
import soot.Body;
import soot.ValueBox;
import soot.jimple.ArrayRef;
import soot.jimple.ConcreteRef;
import soot.jimple.FieldRef;

import java.util.ArrayList;

public class FrameConditionFinder extends BodyTransformer {

    private final static Lazy<FrameConditionFinder> INSTANCE = Lazy.from(FrameConditionFinder::new);

    public static FrameConditionFinder v() {
        return INSTANCE.get();
    }

    private FrameConditionFinder() {
    }

    @Override
    public void transformBody(final BodyContext bodyContext) {
        if (!BehaviorTagMarker.v().hasTag(bodyContext.getSootMethod())) {
            final Body body = bodyContext.getBody();
            final var inferredFrames = new ArrayList<ConcreteRef>();

            for (final ValueBox valueBox : body.getDefBoxes()) {
                if (valueBox.getValue() instanceof final ConcreteRef concreteRef &&
                        (concreteRef instanceof ArrayRef || concreteRef instanceof FieldRef)) {
                    inferredFrames.add(concreteRef);
                }
            }

            final InferredFramesTag inferredFramesTag = new InferredFramesTag(inferredFrames);
            InferredFramesTagAccessor.v().put(body, inferredFramesTag);
        }
    }

}