package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagFlagger;
import soot.SootMethod;

/**
 * Flags the body of a method defining a Behavior expression.
 *
 * @author paganma
 */
public class TwoStateTagFlagger extends TagFlagger<SootMethod, TwoStateTag> {

    private static final Lazy<TwoStateTagFlagger> INSTANCE =
            Lazy.from(() -> new TwoStateTagFlagger(TwoStateTag.v()));

    public static TwoStateTagFlagger v() {
        return INSTANCE.get();
    }

    private TwoStateTagFlagger(final TwoStateTag tag) {
        super(tag);
    }

}