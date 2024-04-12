package byteback.syntax.member.method.body.tag;

import byteback.syntax.tag.TagFlagger;
import byteback.common.function.Lazy;
import soot.Body;

/**
 * Flags the body of a method defining a Behavior expression.
 *
 * @author paganma
 */
public class BehaviorFlagger extends TagFlagger<Body, BehaviorTag> {

    private static final Lazy<BehaviorFlagger> instance =
            Lazy.from(()  -> new BehaviorFlagger(BehaviorTag.v()));

    public static BehaviorFlagger v() {
        return instance.get();
    }

    private BehaviorFlagger(final BehaviorTag tag) {
        super(tag);
    }

}