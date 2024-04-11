package byteback.analysis.global.vimp.syntax.type;

import byteback.common.function.Lazy;
import soot.Type;

public class TypeType extends Type {

    private static final Lazy<TypeType> instance = Lazy.from(TypeType::new);

    public static TypeType v() {
        return instance.get();
    }

    private TypeType() {
    }

    @Override
    public String toString() {
        return "type";
    }

}
