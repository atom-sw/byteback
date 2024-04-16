package byteback.syntax.type;

import byteback.common.function.Lazy;
import soot.Type;


/**
 * A type designating any class type.
 *
 * @author paganma
 */
public class TypeType extends Type implements DefaultCaseType {

    private static final Lazy<TypeType> INSTANCE = Lazy.from(TypeType::new);

    public static TypeType v() {
        return INSTANCE.get();
    }

    private TypeType() {
    }

    @Override
    public String toString() {
        return "type";
    }

}
