package byteback.analysis.model.syntax.type;

import byteback.analysis.model.syntax.type.visitor.TypeSwitch;
import byteback.common.function.Lazy;

public class NullType extends Type {

    public static final int HASHCODE = 0x9891DFE1;

    private static final Lazy<NullType> instance = Lazy.from(NullType::new);

    public static NullType v() {
        return instance.get();
    }

    private NullType() {}

    @Override
    public int hashCode() {
        return HASHCODE;
    }

    @Override
    public String toString() {
        return "null_type";
    }

    @Override
    public void apply(final TypeSwitch<?> typeSwitch) {
        typeSwitch.caseNullType(this);
    }
}
