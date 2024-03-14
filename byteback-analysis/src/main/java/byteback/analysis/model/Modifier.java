package byteback.analysis.model;

/**
 * A class that provides static methods and constants to represent and work with with Java modifiers (ie public, final,...)
 * Represents Java modifiers as int constants that can be packed and combined by bitwise operations and methods to query
 * these.
 */
public class Modifier {
    public static final int ABSTRACT = 0x0400;
    public static final int FINAL = 0x0010;
    public static final int INTERFACE = 0x0200;
    public static final int NATIVE = 0x0100;
    public static final int PRIVATE = 0x0002;
    public static final int PROTECTED = 0x0004;
    public static final int PUBLIC = 0x0001;
    public static final int STATIC = 0x0008;
    public static final int SYNCHRONIZED = 0x0020;
    public static final int TRANSIENT = 0x0080; /* VARARGS for methods */
    public static final int VOLATILE = 0x0040; /* BRIDGE for methods */
    public static final int STRICTFP = 0x0800;
    public static final int ANNOTATION = 0x2000;
    public static final int ENUM = 0x4000;

    private Modifier() {
    }

    public static boolean isAbstract(int m) {
        return (m & ABSTRACT) != 0;
    }

    public static boolean isFinal(int m) {
        return (m & FINAL) != 0;
    }

    public static boolean isInterface(int m) {
        return (m & INTERFACE) != 0;
    }

    public static boolean isNative(int m) {
        return (m & NATIVE) != 0;
    }

    public static boolean isPrivate(int m) {
        return (m & PRIVATE) != 0;
    }

    public static boolean isProtected(int m) {
        return (m & PROTECTED) != 0;
    }

    public static boolean isPublic(int m) {
        return (m & PUBLIC) != 0;
    }

    public static boolean isStatic(int m) {
        return (m & STATIC) != 0;
    }

    public static boolean isSynchronized(int m) {
        return (m & SYNCHRONIZED) != 0;
    }

    public static boolean isTransient(int m) {
        return (m & TRANSIENT) != 0;
    }

    public static boolean isVolatile(int m) {
        return (m & VOLATILE) != 0;
    }

    public static boolean isStrictFP(int m) {
        return (m & STRICTFP) != 0;
    }

    public static boolean isAnnotation(int m) {
        return (m & ANNOTATION) != 0;
    }

    public static boolean isEnum(int m) {
        return (m & ENUM) != 0;
    }

    public static String format(int modifiers) {
        StringBuilder buffer = new StringBuilder();

        if (isPublic(modifiers)) {
            buffer.append("public ");
        } else if (isPrivate(modifiers)) {
            buffer.append("private ");
        } else if (isProtected(modifiers)) {
            buffer.append("protected ");
        }

        if (isAbstract(modifiers)) {
            buffer.append("abstract ");
        }

        if (isStatic(modifiers)) {
            buffer.append("static ");
        }

        if (isFinal(modifiers)) {
            buffer.append("final ");
        }

        if (isSynchronized(modifiers)) {
            buffer.append("synchronized ");
        }

        if (isNative(modifiers)) {
            buffer.append("native ");
        }

        if (isTransient(modifiers)) {
            buffer.append("transient ");
        }

        if (isVolatile(modifiers)) {
            buffer.append("volatile ");
        }

        if (isStrictFP(modifiers)) {
            buffer.append("strictfp ");
        }

        if (isAnnotation(modifiers)) {
            buffer.append("annotation ");
        }

        if (isEnum(modifiers)) {
            buffer.append("enum ");
        }

        if (isInterface(modifiers)) {
            buffer.append("interface ");
        }

        return buffer.toString().trim();
    }
}
