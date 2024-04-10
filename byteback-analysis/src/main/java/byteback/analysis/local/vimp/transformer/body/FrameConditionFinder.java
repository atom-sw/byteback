package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.local.common.transformer.body.BodyTransformer;
import byteback.analysis.local.vimp.tag.body.BehaviorFlagger;
import byteback.analysis.local.vimp.tag.body.InferredLocalFramesProvider;
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
        if (BehaviorFlagger.v().isTagged(body)) {
            final List<ValueBox> inferredFrames = InferredLocalFramesProvider.v().compute(body).getValueBoxes();

            for (final ValueBox valueBox : body.getDefBoxes()) {
                if (valueBox.getValue() instanceof final ConcreteRef concreteRef) {
                    if (concreteRef instanceof ArrayRef || concreteRef instanceof FieldRef) {
                        inferredFrames.add(valueBox);
                    }
                }
            }
        }
    }

}
