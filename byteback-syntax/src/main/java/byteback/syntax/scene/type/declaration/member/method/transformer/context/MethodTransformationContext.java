package byteback.syntax.scene.type.declaration.member.method.transformer.context;

import byteback.syntax.scene.type.declaration.member.method.context.MethodContext;
import byteback.syntax.scene.type.declaration.transformer.context.ClassTransformationContext;
import soot.SootMethod;

public class MethodTransformationContext extends MethodContext<ClassTransformationContext> {

    public MethodTransformationContext(final ClassTransformationContext classContext, final SootMethod sootMethod) {
        super(classContext, sootMethod);
    }

}
