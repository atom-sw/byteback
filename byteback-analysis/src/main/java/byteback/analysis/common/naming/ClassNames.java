package byteback.analysis.common.naming;

public class ClassNames {

    public static String stripConstantDescriptor(final String descriptor) {
        return stripDescriptor(descriptor.replace("[", "").replace("]", ""));
    }

    public static String stripDescriptor(final String descriptor) {
        return descriptor.substring(1, descriptor.length() - 1).replace("/", ".");
    }

    public static String stripLabelDescriptor(final String descriptor) {
        return stripDescriptor(descriptor.substring(1));
    }

    public static String toQualifiedName(final String internalName) {
        return internalName.replace('/', '.');
    }

    public static String toInternalName(final String qualifiedName) {
        return qualifiedName.replace('.', '/');
    }


}
