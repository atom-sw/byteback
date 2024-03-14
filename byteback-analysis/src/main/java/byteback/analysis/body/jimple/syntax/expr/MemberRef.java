package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.model.syntax.signature.MemberSignature;
import byteback.analysis.model.syntax.type.ClassType;

public abstract class MemberRef<T extends MemberSignature> implements ConcreteRef {

    protected final T signature;

    public MemberRef(final T signature) {
        this.signature = signature;
    }

    public T getSignature() {
        return signature;
    }

    public ClassType getDeclaringClassType() {
        return signature.getDeclaringClassType();
    }
}
