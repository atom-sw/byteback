package byteback.syntax.scene.type.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.HeapType;
import byteback.syntax.scene.type.TypeType;
import byteback.syntax.scene.type.encoder.TypeAccessEncoder;
import soot.*;

public class TypeAccessToBplEncoder extends TypeAccessEncoder {

	public TypeAccessToBplEncoder(final Printer printer) {
		super(printer);
	}

	@Override
	public void encodeTypeAccess(final Type type) {
		if (type instanceof BooleanType) {
			printer.print("`boolean`");
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

		if (type instanceof CharType) {
			printer.print("`char`");
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

		if (type instanceof RefLikeType) {
			printer.print("Reference");
			return;
		}

		if (type instanceof TypeType) {
			printer.print("Type");
			return;
		}

		if (type instanceof HeapType) {
			printer.print("Store");
			return;
		}

		throw new IllegalStateException("Unable to encode type " + type + ".");
	}

}
