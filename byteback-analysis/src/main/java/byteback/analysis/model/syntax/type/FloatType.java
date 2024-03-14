package byteback.analysis.model.syntax.type;

import byteback.common.function.Lazy;

public class FloatType extends Type implements PrimitiveType {

    public static final int HASHCODE = 0xA84373FA;

    private static final Lazy<FloatType> instance = Lazy.from(FloatType::new);

    public static FloatType v() {
        return instance.get();
    }

    private FloatType() {}

    @Override
    public int hashCode() {
        return HASHCODE;
    }

    @Override
    public String toString() {
        return "float";
    }
}
