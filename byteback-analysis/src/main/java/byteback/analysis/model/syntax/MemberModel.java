package byteback.analysis.model.syntax;

import byteback.analysis.model.syntax.signature.MemberSignature;

public abstract class MemberModel<T extends MemberSignature> extends Model<T> {

    public MemberModel(final int modifiers, final T memberSignature) {
        super(modifiers, memberSignature);
    }
}
