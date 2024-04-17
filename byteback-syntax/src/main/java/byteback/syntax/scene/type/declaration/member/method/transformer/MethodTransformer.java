package byteback.syntax.scene.type.declaration.member.method.transformer;

import byteback.syntax.scene.type.declaration.member.method.transformer.context.MethodTransformationContext;
import byteback.syntax.scene.type.declaration.transformer.ClassTransformer;
import byteback.syntax.scene.type.declaration.transformer.context.ClassTransformationContext;
import soot.SootClass;
import soot.SootMethod;

import java.util.ArrayList;
import java.util.List;

public abstract class MethodTransformer extends ClassTransformer {

    public abstract void transformMethod(final MethodTransformationContext methodContext);

    @Override
    public void transformClass(final ClassTransformationContext classTransformationContext) {
        final SootClass sootClass = classTransformationContext.getSootClass();

        if (sootClass.resolvingLevel() >= SootClass.SIGNATURES) {
            final List<SootMethod> methods = sootClass.getMethods();

            for (final SootMethod sootMethod : new ArrayList<>(methods)) {
                final var methodTransformationContext = new MethodTransformationContext(classTransformationContext, sootMethod);
                transformMethod(methodTransformationContext);
            }
        }
    }

}
