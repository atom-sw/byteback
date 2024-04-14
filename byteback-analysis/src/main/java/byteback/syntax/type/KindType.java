package byteback.syntax.type;

import byteback.common.function.Lazy;
import soot.Type;


/**
 * A type designating any class type.
 *
 * @author paganma
 */
public class KindType extends Type implements DefaultCaseType {

    private static final Lazy<KindType> INSTANCE = Lazy.from(KindType::new);

    public static KindType v() {
        return INSTANCE.get();
    }

    private KindType() {
    }

    @Override
    public String toString() {
        return "type";
    }

}
