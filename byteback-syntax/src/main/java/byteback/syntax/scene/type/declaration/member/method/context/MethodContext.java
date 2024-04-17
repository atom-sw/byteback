package byteback.syntax.scene.type.declaration.member.method.context;

import byteback.syntax.scene.type.declaration.context.ClassContext;
import byteback.syntax.scene.type.declaration.member.context.MemberContext;
import soot.SootMethod;

public class MethodContext extends MemberContext {

    private final SootMethod sootMethod;

    public MethodContext(final ClassContext classContext, final SootMethod sootMethod) {
        super(classContext);
        this.sootMethod = sootMethod;
    }

    public SootMethod getSootMethod() {
        return sootMethod;
    }

}
