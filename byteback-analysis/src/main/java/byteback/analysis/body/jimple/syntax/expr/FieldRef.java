package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.model.syntax.signature.FieldSignature;
import byteback.analysis.model.syntax.type.Type;

public abstract class FieldRef extends MemberRef<FieldSignature> {

    public FieldRef(final FieldSignature signature) {
        super(signature);
    }

    public Type getType() {
        return getSignature().getType();
    }
}
