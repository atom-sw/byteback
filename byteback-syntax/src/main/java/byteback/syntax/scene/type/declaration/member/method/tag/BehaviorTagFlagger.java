package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagFlagger;
import soot.SootMethod;

/**
 * Flags the body of a method defining a Behavior expression.
 *
 * @author paganma
 */
public class BehaviorTagFlagger extends TagFlagger<SootMethod, BehaviorTag> {

    private static final Lazy<BehaviorTagFlagger> INSTANCE =
            Lazy.from(() -> new BehaviorTagFlagger(BehaviorTag.v()));

    public static BehaviorTagFlagger v() {
        return INSTANCE.get();
    }

    private BehaviorTagFlagger(final BehaviorTag tag) {
        super(tag);
    }

}