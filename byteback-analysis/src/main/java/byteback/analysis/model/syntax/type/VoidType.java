package byteback.analysis.model.syntax.type;

import byteback.analysis.model.syntax.type.visitor.TypeSwitch;
import byteback.common.function.Lazy;

public class VoidType extends Type {

    public static final int HASHCODE = 0x3A8C1035;

    private static final Lazy<VoidType> instance = Lazy.from(VoidType::new);

    public static VoidType v() {
        return instance.get();
    }

    private VoidType() {}

    @Override
    public int hashCode() {
        return HASHCODE;
    }

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public void apply(final TypeSwitch<?> typeSwitch) {
        typeSwitch.caseVoidType(this);
    }
}
