package byteback.analysis.model.syntax.type;

import byteback.analysis.model.syntax.type.visitor.TypeSwitch;
import byteback.common.function.Lazy;

public class DoubleType extends Type implements PrimitiveType {

    public static final int HASHCODE = 0x4B9D7242;

    private static final Lazy<DoubleType> instance = Lazy.from(DoubleType::new);

    public static DoubleType v() {
        return instance.get();
    }

    private DoubleType() {}

    @Override
    public int hashCode() {
        return HASHCODE;
    }

    @Override
    public String toString() {
        return "double";
    }

    @Override
    public void apply(final TypeSwitch<?> typeSwitch) {
        typeSwitch.caseDoubleType(this);
    }
}
