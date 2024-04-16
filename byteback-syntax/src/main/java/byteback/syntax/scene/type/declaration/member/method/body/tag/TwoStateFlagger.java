package byteback.syntax.scene.type.declaration.member.method.body.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagFlagger;
import soot.Body;

/**
 * Flags the body of a method defining a Behavior expression.
 *
 * @author paganma
 */
public class TwoStateFlagger extends TagFlagger<Body, BehaviorTag> {

    private static final Lazy<TwoStateFlagger> INSTANCE =
            Lazy.from(() -> new TwoStateFlagger(BehaviorTag.v()));

    public static TwoStateFlagger v() {
        return INSTANCE.get();
    }

    private TwoStateFlagger(final BehaviorTag tag) {
        super(tag);
    }

}