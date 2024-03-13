package byteback.analysis.model.syntax;

import byteback.analysis.model.syntax.signature.MethodSignature;

public class MethodModel extends MemberModel<MethodSignature> {

    public MethodModel(final int modifiers, final MethodSignature methodSignature) {
        super(modifiers, methodSignature);
    }
}
