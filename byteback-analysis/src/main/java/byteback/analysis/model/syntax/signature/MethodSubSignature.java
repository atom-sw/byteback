package byteback.analysis.model.syntax.signature;

import byteback.analysis.model.syntax.type.ClassType;
import byteback.analysis.model.syntax.type.Type;

import java.util.List;

public class MethodSubSignature extends MemberSignature {

    private final List<Type> argumentTypes;

    public MethodSubSignature(final String name, final List<Type> argumentTypes, final ClassType declaringClassType) {
        super(name, declaringClassType);
        this.argumentTypes = argumentTypes;
    }

    public List<Type> getArgumentTypes() {
        return argumentTypes;
    }

}
