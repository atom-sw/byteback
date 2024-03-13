package byteback.analysis.model.syntax.type;

import byteback.analysis.model.syntax.type.visitor.TypeSwitch;
import byteback.common.function.Lazy;

public class ShortType extends Type implements PrimitiveType, IntegerType {

    public static final int HASHCODE = 0x8B817DD3;

    private static final Lazy<ShortType> instance = Lazy.from(ShortType::new);

    public static ShortType v() {
        return instance.get();
    }

    private ShortType() {}

    @Override
    public int hashCode() {
        return HASHCODE;
    }

    @Override
    public String toString() {
        return "short";
    }

    @Override
    public void apply(final TypeSwitch<?> typeSwitch) {
        typeSwitch.caseShortType(this);
    }
}
