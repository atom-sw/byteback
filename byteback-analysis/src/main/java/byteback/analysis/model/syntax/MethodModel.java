package byteback.analysis.model.syntax;

import byteback.analysis.model.syntax.signature.MethodSignature;
import byteback.analysis.model.syntax.type.Type;

public class MethodModel extends MemberModel<MethodSignature> {

    public MethodModel(final int modifiers, final MethodSignature methodSignature) {
        super(modifiers, methodSignature);
    }

    public int getParameterCount() {
        return getSignature().getArgumentTypes().size();
    }

    public Type getReturnType() {
        return signature.getReturnType();
    }
}
