package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.syntax.scene.type.declaration.context.ClassContext;
import byteback.syntax.scene.type.declaration.member.method.context.MethodContext;
import byteback.syntax.scene.type.declaration.transformer.ClassTransformer;
import soot.SootClass;
import soot.SootMethod;

import java.util.ArrayList;
import java.util.List;

public abstract class MethodTransformer extends ClassTransformer {

    public abstract void transformMethod(final MethodContext methodContext);

    @Override
    public void transformClass(final ClassContext classContext) {
        final SootClass sootClass = classContext.getSootClass();

        if (sootClass.resolvingLevel() >= SootClass.SIGNATURES) {
            final List<SootMethod> methods = sootClass.getMethods();

            for (final SootMethod sootMethod : new ArrayList<>(methods)) {
                final var methodContext = new MethodContext(classContext, sootMethod);
                transformMethod(methodContext);
            }
        }
    }

}
