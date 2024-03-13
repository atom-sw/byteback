package byteback.analysis.common.naming;

import byteback.analysis.model.syntax.type.*;
import byteback.common.function.Lazy;

public class TypeNames {

    private final static Lazy<TypeNames> instance = Lazy.from(TypeNames::new);

    private TypeNames() {}

    public static TypeNames v() {
        return instance.get();
    }

    public Type toBaseType(String internalName) {
        if (internalName.charAt(0) == '[') {
            internalName = internalName.substring(internalName.lastIndexOf('[') + 1);
        }

        if (internalName.charAt(internalName.length() - 1) == ';') {
            internalName = internalName.substring(0, internalName.length() - 1);
            if (internalName.charAt(0) == 'L') {
                internalName = internalName.substring(1);
            }
            internalName = ClassNames.toQualifiedName(internalName);
            return new ClassType(internalName);
        }

        switch (internalName.charAt(0)) {
            case 'Z':
                return BooleanType.v();
            case 'B':
                return ByteType.v();
            case 'C':
                return CharType.v();
            case 'S':
                return ShortType.v();
            case 'I':
                return IntType.v();
            case 'F':
                return FloatType.v();
            case 'J':
                return LongType.v();
            case 'D':
                return DoubleType.v();
            default:
                internalName = ClassNames.toQualifiedName(internalName);
                return new ClassType(internalName);
        }
    }
}
