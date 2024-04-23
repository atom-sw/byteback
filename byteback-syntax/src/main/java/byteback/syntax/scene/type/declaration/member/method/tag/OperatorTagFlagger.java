package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagFlagger;
import soot.SootMethod;

public class OperatorTagFlagger extends TagFlagger<SootMethod, OperatorTag> {

    private static final Lazy<OperatorTagFlagger> INSTANCE =
            Lazy.from(() -> new OperatorTagFlagger(OperatorTag.v()));

    public static OperatorTagFlagger v() {
        return INSTANCE.get();
    }

    private OperatorTagFlagger(final OperatorTag tag) {
        super(tag);
    }

}