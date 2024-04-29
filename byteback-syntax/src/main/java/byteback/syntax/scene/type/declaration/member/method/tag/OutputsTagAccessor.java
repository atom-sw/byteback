package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagAccessor;
import soot.SootMethod;

public class OutputsTagAccessor extends TagAccessor<SootMethod, InputsTag> {

    private static final Lazy<OutputsTagAccessor> INSTANCE =
            Lazy.from(() -> new OutputsTagAccessor(OutputsTag.NAME));

    public static OutputsTagAccessor v() {
        return INSTANCE.get();
    }

    private OutputsTagAccessor(final String tagName) {
        super(tagName);
    }

}