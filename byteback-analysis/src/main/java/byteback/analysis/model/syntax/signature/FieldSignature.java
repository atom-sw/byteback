package byteback.analysis.model.syntax.signature;

import byteback.analysis.model.syntax.type.ClassType;
import byteback.analysis.model.syntax.type.Type;

public class FieldSignature extends MemberSignature {

    private final Type type;

    public FieldSignature(final String name, final Type type, final int modifiers, final ClassType declaringClassType) {
        super(name, declaringClassType);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

}
