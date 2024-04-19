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
public class TwoStateFlagger extends TagFlagger<SootMethod, TwoStateTag> {

    private static final Lazy<TwoStateFlagger> INSTANCE =
            Lazy.from(() -> new TwoStateFlagger(TwoStateTag.v()));

    public static TwoStateFlagger v() {
        return INSTANCE.get();
    }

    private TwoStateFlagger(final TwoStateTag tag) {
        super(tag);
    }

}