package byteback.syntax.name;

import byteback.common.function.Lazy;

/**
 * Contains utility methods for working with class names.
 *
 * @author paganma
 */
public class ClassNames {

    public static final Lazy<ClassNames> INSTANCE = Lazy.from(ClassNames::new);

    public static ClassNames v() {
        return INSTANCE.get();
    }

    private ClassNames() {
    }

    public String stripDescriptor(final String descriptor) {
        return descriptor.substring(1, descriptor.length() - 1).replace("/", ".");
    }

}
