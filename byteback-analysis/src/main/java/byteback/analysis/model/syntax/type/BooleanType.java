package byteback.analysis.model.syntax.type;

import byteback.analysis.model.syntax.type.visitor.TypeSwitch;
import byteback.common.function.Lazy;

public class BooleanType extends Type implements PrimitiveType, IntegerType {

    public static final int HASHCODE = 0x1C4585DA;

    private static final Lazy<BooleanType> instance = Lazy.from(BooleanType::new);

    public static BooleanType v() {
        return instance.get();
    }

    private BooleanType() {}

    @Override
    public int hashCode() {
        return HASHCODE;
    }

    @Override
    public String toString() {
        return "boolean";
    }

    @Override
    public void apply(final TypeSwitch<?> typeSwitch) {
        typeSwitch.caseBooleanType(this);
    }
}
