package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagAccessor;
import soot.SootMethod;

public class InputsTagAccessor extends TagAccessor<SootMethod, InputsTag> {

    private static final Lazy<InputsTagAccessor> INSTANCE =
            Lazy.from(() -> new InputsTagAccessor(InputsTag.NAME));

    public static InputsTagAccessor v() {
        return INSTANCE.get();
    }

    private InputsTagAccessor(final String tagName) {
        super(tagName);
    }

}