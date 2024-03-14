package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.model.syntax.signature.MethodSignature;
import byteback.analysis.model.syntax.type.Type;

public abstract class MethodRef extends MemberRef<MethodSignature> {

    public MethodRef(final MethodSignature signature) {
        super(signature);
    }

    public Type getReturnType() {
        return getSignature().getReturnType();
    }
}
