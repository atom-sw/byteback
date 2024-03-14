package byteback.analysis.model.syntax.type;

import byteback.common.function.Lazy;

public class CharType extends Type implements PrimitiveType, IntegerType {

    public static final int HASHCODE = 0x739EA474;

    private static final Lazy<CharType> instance = Lazy.from(CharType::new);

    public static CharType v() {
        return instance.get();
    }

    private CharType() {}

    @Override
    public String toString() {
        return "char";
    }

    @Override
    public int hashCode() {
        return HASHCODE;
    }
}
