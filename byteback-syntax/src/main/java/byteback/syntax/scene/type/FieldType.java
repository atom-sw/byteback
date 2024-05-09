package byteback.syntax.scene.type;

import byteback.common.function.Lazy;
import soot.Type;


/**
 * A type for a heap reference.
 *
 * @author paganma
 */
public class FieldType extends Type implements DefaultCaseType {

    private static final Lazy<FieldType> INSTANCE = Lazy.from(FieldType::new);

    public static FieldType v() {
        return INSTANCE.get();
    }

    private FieldType() {
    }

    @Override
    public String toString() {
        return "field";
    }

}
