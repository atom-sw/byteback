package byteback.analysis.model.syntax.type;

import byteback.common.function.Lazy;

public class ByteType extends Type implements PrimitiveType, IntegerType {

    public static final int HASHCODE = 0x813D1329;

    private static final Lazy<ByteType> instance = Lazy.from(ByteType::new);

    public static ByteType v() {
        return instance.get();
    }

    private ByteType() {}

    @Override
    public int hashCode() {
        return HASHCODE;
    }

    @Override
    public String toString() {
        return "byte";
    }
}
