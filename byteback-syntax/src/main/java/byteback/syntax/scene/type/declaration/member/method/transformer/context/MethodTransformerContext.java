package byteback.syntax.scene.type.declaration.member.method.transformer.context;

import byteback.syntax.scene.type.declaration.member.method.context.MethodContext;
import byteback.syntax.scene.type.declaration.transformer.context.ClassTransformerContext;
import soot.SootMethod;

public class MethodTransformerContext extends MethodContext<ClassTransformerContext> {

    public MethodTransformerContext(final ClassTransformerContext classContext, final SootMethod sootMethod) {
        super(classContext, sootMethod);
    }

}
