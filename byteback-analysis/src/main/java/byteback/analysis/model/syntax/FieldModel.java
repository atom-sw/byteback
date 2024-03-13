package byteback.analysis.model.syntax;

import byteback.analysis.model.syntax.signature.FieldSignature;

public class FieldModel extends MemberModel<FieldSignature> {

    public FieldModel(final int modifiers, final FieldSignature fieldSignature) {
        super(modifiers, fieldSignature);
    }
}
