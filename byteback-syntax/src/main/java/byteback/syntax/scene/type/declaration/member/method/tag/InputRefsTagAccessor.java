package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagAccessor;
import soot.SootMethod;

public class InputRefsTagAccessor extends TagAccessor<SootMethod, InputRefsTag> {

    private static final Lazy<InputRefsTagAccessor> INSTANCE =
            Lazy.from(() -> new InputRefsTagAccessor(InputRefsTag.NAME));

    public static InputRefsTagAccessor v() {
        return INSTANCE.get();
    }

    private InputRefsTagAccessor(final String tagName) {
        super(tagName);
    }

}