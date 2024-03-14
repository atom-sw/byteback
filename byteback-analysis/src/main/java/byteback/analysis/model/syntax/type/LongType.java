package byteback.analysis.model.syntax.type;

import byteback.common.function.Lazy;

public class LongType extends Type implements PrimitiveType {

    public static final int HASHCODE = 0x023DA077;

    private static final Lazy<LongType> instance = Lazy.from(LongType::new);

    public static LongType v() {
        return instance.get();
    }

    private LongType() {}

    @Override
    public int hashCode() {
        return HASHCODE;
    }

    @Override
    public String toString() {
        return "long";
    }
}
