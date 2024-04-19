package byteback.syntax.scene.type.declaration.member.method.body.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagFlagger;
import soot.Body;
import soot.SootMethod;

/**
 * @author paganma
 */
public class ExceptionalFlagger extends TagFlagger<SootMethod, ExceptionalTag> {

    private static final Lazy<ExceptionalFlagger> INSTANCE =
            Lazy.from(() -> new ExceptionalFlagger(ExceptionalTag.v()));

    public static ExceptionalFlagger v() {
        return INSTANCE.get();
    }

    private ExceptionalFlagger(final ExceptionalTag tag) {
        super(tag);
    }

}
