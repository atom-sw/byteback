package byteback.syntax.scene.type.declaration.member.method.context;

import byteback.syntax.scene.type.declaration.context.ClassContext;
import byteback.syntax.scene.type.declaration.member.context.MemberContext;
import soot.SootMethod;

public abstract class MethodContext<C extends ClassContext<?>> extends MemberContext<C> {

    private final SootMethod sootMethod;

    public MethodContext(final C classContext, final SootMethod sootMethod) {
        super(classContext);
        this.sootMethod = sootMethod;
    }

    public SootMethod getSootMethod() {
        return sootMethod;
    }

}
