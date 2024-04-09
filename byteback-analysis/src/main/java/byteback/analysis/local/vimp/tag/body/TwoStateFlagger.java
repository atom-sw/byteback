package byteback.analysis.local.vimp.tag.body;

import byteback.analysis.common.tag.TagFlagger;
import byteback.common.function.Lazy;
import soot.Body;

/**
 * Flags the body of a method defining a Behavior expression.
 *
 * @author paganma
 */
public class TwoStateFlagger extends TagFlagger<Body, BehaviorTag> {

    private static final Lazy<TwoStateFlagger> instance =
            Lazy.from(()  -> new TwoStateFlagger(BehaviorTag.v()));

    public static TwoStateFlagger v() {
        return instance.get();
    }

    private TwoStateFlagger(final BehaviorTag tag) {
        super(tag);
    }

}