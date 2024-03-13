package byteback.analysis.model.syntax.signature;

import byteback.analysis.model.syntax.type.ClassType;
import byteback.analysis.model.syntax.type.Type;

public abstract class MemberSignature implements Signature {

    private final String name;

    private final ClassType declaringClassType;

    public MemberSignature(final String name, final ClassType declaringClassType) {
        this.name = name;
        this.declaringClassType = declaringClassType;
    }

    public String getName() {
        return name;
    }

    public ClassType getDeclaringClassType() {
        return declaringClassType;
    }
}
