package byteback.syntax.type.declaration.method.context;

import byteback.syntax.type.declaration.context.ClassContext;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

public class MethodContext extends ClassContext {

    private final SootMethod sootMethod;

    public MethodContext(final Scene scene, final SootClass sootClass, final SootMethod sootMethod) {
        super(scene, sootClass);
        this.sootMethod = sootMethod;
    }

    public MethodContext(final ClassContext classContext, final SootMethod sootMethod) {
        this(classContext.getScene(), classContext.getSootClass(), sootMethod);
    }

    public SootMethod getSootMethod() {
        return sootMethod;
    }

}
