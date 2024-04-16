package byteback.syntax.scene.type.declaration.member.context;

import byteback.syntax.context.Context;
import byteback.syntax.scene.type.declaration.context.ClassContext;

public abstract class MemberContext<C extends ClassContext<?>> implements Context {

    private final C classContext;

    public MemberContext(final C classContext) {
        this.classContext = classContext;
    }

    public C getClassContext() {
        return classContext;
    }

}
