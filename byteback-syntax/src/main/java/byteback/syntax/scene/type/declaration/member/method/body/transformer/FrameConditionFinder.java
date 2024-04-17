package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.tag.BehaviorFlagger;
import byteback.syntax.scene.type.declaration.member.method.body.tag.InferredLocalFramesProvider;
import byteback.syntax.scene.type.declaration.member.method.body.tag.InferredLocalFramesTag;
import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.context.BodyTransformationContext;
import soot.Body;
import soot.ValueBox;
import soot.jimple.ArrayRef;
import soot.jimple.ConcreteRef;
import soot.jimple.FieldRef;

import java.util.List;

public class FrameConditionFinder extends BodyTransformer {

    private final static Lazy<FrameConditionFinder> INSTANCE = Lazy.from(FrameConditionFinder::new);

    public static FrameConditionFinder v() {
        return INSTANCE.get();
    }

    private FrameConditionFinder() {
    }

    @Override
    public void transformBody(final BodyTransformationContext bodyContext) {
        final Body body = bodyContext.getBody();

        if (BehaviorFlagger.v().isTagged(body)) {
            final InferredLocalFramesTag inferredLocalFramesTag = InferredLocalFramesProvider.v().compute(body);
            final List<ConcreteRef> inferredFrames = inferredLocalFramesTag.getValues();

            for (final ValueBox valueBox : body.getDefBoxes()) {
                if (valueBox.getValue() instanceof final ConcreteRef concreteRef &&
                        (concreteRef instanceof ArrayRef || concreteRef instanceof FieldRef)) {
                    inferredFrames.add(concreteRef);
                }
            }
        }
    }

}
