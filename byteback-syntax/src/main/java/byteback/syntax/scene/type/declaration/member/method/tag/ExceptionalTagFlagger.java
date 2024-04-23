package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagFlagger;
import soot.SootMethod;

/**
 * @author paganma
 */
public class ExceptionalTagFlagger extends TagFlagger<SootMethod, ExceptionalTag> {

    private static final Lazy<ExceptionalTagFlagger> INSTANCE =
            Lazy.from(() -> new ExceptionalTagFlagger(ExceptionalTag.v()));

    public static ExceptionalTagFlagger v() {
        return INSTANCE.get();
    }

    private ExceptionalTagFlagger(final ExceptionalTag tag) {
        super(tag);
    }

}
