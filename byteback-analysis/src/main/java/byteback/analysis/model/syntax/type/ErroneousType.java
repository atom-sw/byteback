package byteback.analysis.model.syntax.type;

import byteback.common.function.Lazy;

public class ErroneousType extends Type implements PrimitiveType {

    public static final int HASHCODE = 0x92473FFF;

    private static final Lazy<ErroneousType> instance = Lazy.from(ErroneousType::new);

    public static ErroneousType v() {
        return instance.get();
    }

    @Override
    public int hashCode() {
        return HASHCODE;
    }

    @Override
    public String toString() {
        return "<error>";
    }
}
