package byteback.analysis.model.syntax.type;

import byteback.analysis.model.syntax.type.visitor.TypeSwitch;
import byteback.common.function.Lazy;

public class UnknownType extends Type {

    public static final int HASHCODE = 0x5CAE5357;

    private static final Lazy<UnknownType> instance = Lazy.from(UnknownType::new);

    public static UnknownType v() {
        return instance.get();
    }

    private UnknownType() {}

    @Override
    public String toString() {
        return "unknown";
    }

    @Override
    public void apply(final TypeSwitch<?> typeSwitch) {
        typeSwitch.caseUnknownType(this);
    }

}
