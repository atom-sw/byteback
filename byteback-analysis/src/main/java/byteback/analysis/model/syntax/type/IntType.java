package byteback.analysis.model.syntax.type;

import byteback.common.function.Lazy;

public class IntType extends Type implements PrimitiveType, IntegerType {

    public static final int HASHCODE = 0xB747239F;

    private static final Lazy<IntType> instance = Lazy.from(IntType::new);

    private IntType() {
    }

    public static IntType v() {
        return instance.get();
    }

    @Override
    public int hashCode() {
        return HASHCODE;
    }

    @Override
    public String toString() {
        return "int";
    }
}
