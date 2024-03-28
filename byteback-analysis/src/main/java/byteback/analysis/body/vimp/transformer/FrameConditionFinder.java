package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.vimp.tag.BehaviorBodyFlagger;
import byteback.analysis.body.vimp.tag.InferredLocalFramesProvider;
import byteback.common.function.Lazy;
import soot.Body;
import soot.ValueBox;
import soot.jimple.ArrayRef;
import soot.jimple.ConcreteRef;
import soot.jimple.FieldRef;

import java.util.List;

public class FrameConditionFinder extends BodyTransformer {

    private final static Lazy<FrameConditionFinder> instance = Lazy.from(FrameConditionFinder::new);

    public static FrameConditionFinder v() {
        return instance.get();
    }

    private FrameConditionFinder() {
    }

    @Override
    public void transformBody(final Body body) {
        if (BehaviorBodyFlagger.v().isTagged(body)) {
            final List<ConcreteRef> inferredFrames = InferredLocalFramesProvider.v().compute(body).getValues();

            for (final ValueBox valueBox : body.getDefBoxes()) {
                if (valueBox.getValue() instanceof final ConcreteRef concreteRef) {
                    if (concreteRef instanceof ArrayRef || concreteRef instanceof FieldRef) {
                        inferredFrames.add(concreteRef);
                    }
                }
            }
        }
    }

}
