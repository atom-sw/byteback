package byteback.syntax.scene.type.encoder.to_bpl;

import byteback.common.function.Lazy;
import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.TypeType;
import byteback.syntax.scene.type.encoder.TypeAccessEncoder;
import soot.*;

public class TypeAccessToBplEncoder implements TypeAccessEncoder {

    private final static Lazy<TypeAccessToBplEncoder> INSTANCE = Lazy.from(TypeAccessToBplEncoder::new);

    public static TypeAccessToBplEncoder v() {
        return INSTANCE.get();
    }

    private TypeAccessToBplEncoder() {
    }

    @Override
    public void encodeTypeAccess(final Printer printer, final Type type) {
        if (type instanceof BooleanType) {
            printer.print("bool");
            return;
        }

        if (type instanceof ByteType) {
            printer.print("`byte`");
            return;
        }

        if (type instanceof ShortType) {
            printer.print("`short`");
            return;
        }

        if (type instanceof IntType) {
            printer.print("`int`");
            return;
        }

        if (type instanceof LongType) {
            printer.print("`long`");
            return;
        }

        if (type instanceof DoubleType) {
            printer.print("`double`");
            return;
        }

        if (type instanceof FloatType) {
            printer.print("`float`");
            return;
        }

        if (type instanceof RefType) {
            printer.print("Reference");
            return;
        }

        if (type instanceof ArrayType) {
            printer.print("Reference");
            return;
        }

        if (type instanceof TypeType) {
            printer.print("Type");
            return;
        }

        throw new IllegalStateException("Unable to encode type " + type);
    }

}
