package byteback.syntax.scene.type.declaration.member.context;

import byteback.syntax.context.Context;
import byteback.syntax.scene.type.declaration.context.ClassContext;

public abstract class MemberContext implements Context {

    private final ClassContext classContext;

    public MemberContext(final ClassContext classContext) {
        this.classContext = classContext;
    }

    public ClassContext getClassContext() {
        return classContext;
    }

}
