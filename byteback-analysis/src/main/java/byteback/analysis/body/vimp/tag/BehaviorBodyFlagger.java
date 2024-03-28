package byteback.analysis.body.vimp.tag;

import byteback.analysis.common.tag.TagFlagger;
import byteback.common.function.Lazy;
import soot.Body;

public class BehaviorBodyFlagger extends TagFlagger<Body, BehaviorBodyTag> {

    private static final Lazy<BehaviorBodyFlagger> instance =
            Lazy.from(()  -> new BehaviorBodyFlagger(BehaviorBodyTag.v()));

    public static BehaviorBodyFlagger v() {
        return instance.get();
    }

    private BehaviorBodyFlagger(final BehaviorBodyTag tag) {
        super(tag);
    }

}