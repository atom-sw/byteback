package byteback.analysis.model.syntax.signature;

import byteback.analysis.model.syntax.type.ClassType;
import byteback.analysis.model.syntax.type.Type;

import java.util.List;

public class MethodSignature extends MethodSubSignature {

    private final Type returnType;

    public MethodSignature(final String name, final Type returnType, final List<Type> argumentTypes,
                           final ClassType declaringClassType) {
        super(name, argumentTypes, declaringClassType);
        this.returnType = returnType;
    }

    public Type getReturnType() {
        return returnType;
    }

}
