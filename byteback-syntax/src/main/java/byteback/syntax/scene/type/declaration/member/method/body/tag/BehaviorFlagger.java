package byteback.syntax.scene.type.declaration.member.method.body.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagFlagger;
import soot.Body;
import soot.SootMethod;

/**
 * Flags the body of a method defining a Behavior expression.
 *
 * @author paganma
 */
public class BehaviorFlagger extends TagFlagger<SootMethod, BehaviorTag> {

    private static final Lazy<BehaviorFlagger> INSTANCE =
            Lazy.from(() -> new BehaviorFlagger(BehaviorTag.v()));

    public static BehaviorFlagger v() {
        return INSTANCE.get();
    }

    private BehaviorFlagger(final BehaviorTag tag) {
        super(tag);
    }

}