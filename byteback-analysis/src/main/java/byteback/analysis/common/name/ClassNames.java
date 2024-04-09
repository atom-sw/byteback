package byteback.analysis.common.name;

import byteback.common.function.Lazy;

/**
 * Contains utility methods for working with class names.
 *
 * @author paganma
 */
public class ClassNames {

    public static final Lazy<ClassNames> instance = Lazy.from(ClassNames::new);

    public static ClassNames v() {
        return instance.get();
    }

    private ClassNames() {
		}

    public String stripConstantDescriptor(final String descriptor) {
        return stripDescriptor(descriptor.replace("[", "").replace("]", ""));
    }

    public String stripDescriptor(final String descriptor) {
        return descriptor.substring(1, descriptor.length() - 1).replace("/", ".");
    }

    public String stripLabelDescriptor(final String descriptor) {
        return stripDescriptor(descriptor.substring(1));
    }

}
