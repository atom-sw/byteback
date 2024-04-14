package byteback.syntax.type.declaration.method.walker;

import byteback.syntax.scene.context.SceneContext;
import byteback.syntax.type.declaration.context.ClassContext;
import byteback.syntax.type.declaration.method.context.MethodContext;
import byteback.syntax.type.declaration.walker.ClassWalker;
import soot.SootClass;
import soot.SootMethod;

import java.util.ArrayList;
import java.util.List;

public abstract class MethodWalker<S extends SceneContext, C extends ClassContext, M extends MethodContext>
        extends ClassWalker<S, C> {

    public abstract M makeMethodContext(final ClassContext classContext, final SootMethod sootMethod);

    public abstract void walkMethod(final M methodContext);

    @Override
    public void walkClass(final C classContext) {
        final SootClass sootClass = classContext.getSootClass();

        if (sootClass.resolvingLevel() >= SootClass.SIGNATURES) {
            final List<SootMethod> methods = sootClass.getMethods();

            for (final SootMethod sootMethod : new ArrayList<>(methods)) {
                final M methodContext = makeMethodContext(classContext, sootMethod);
                walkMethod(methodContext);
            }
        }
    }

}
