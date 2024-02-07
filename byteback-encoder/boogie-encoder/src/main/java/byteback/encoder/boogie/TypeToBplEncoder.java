package byteback.encoder.boogie;

import sootup.core.types.ArrayType;
import sootup.core.types.ClassType;
import sootup.core.types.PrimitiveType.BooleanType;
import sootup.core.types.PrimitiveType.ByteType;
import sootup.core.types.PrimitiveType.DoubleType;
import sootup.core.types.PrimitiveType.FloatType;
import sootup.core.types.PrimitiveType.IntType;
import sootup.core.types.PrimitiveType.LongType;
import sootup.core.types.PrimitiveType.ShortType;
import sootup.core.types.ReferenceType;
import sootup.core.types.Type;

public interface TypeToBplEncoder extends BplIdentifierEncoder {

	default void encodeClassTypeIdentifier(final ClassType classType) {
		encodeIdentifier(classType.getFullyQualifiedName());
	}

	default void encodeBooleanTypeAccess() {
		encode(BplPrelude.BOOLEAN_TYPE);
	}

	default void encodeByteTypeAccess() {
		encode(BplPrelude.BYTE_TYPE);
	}

	default void encodeShortTypeAccess() {
		encode(BplPrelude.SHORT_TYPE);
	}

	default void encodeIntTypeAccess() {
		encode(BplPrelude.INT_TYPE);
	}

	default void encodeLongTypeAccess() {
		encode(BplPrelude.LONG_TYPE);
	}

	default void encodeDoubleTypeAccess() {
		encode(BplPrelude.DOUBLE_TYPE);
	}

	default void encodeFloatTypeAccess() {
		encode(BplPrelude.FLOAT_TYPE);
	}

	default void encodeReferenceTypeAccess() {
		encode(BplPrelude.REFERENCE_TYPE);
	}

	default void encodeTypeAccess(final Type type) {
		if (type instanceof ReferenceType) {
			encodeReferenceTypeAccess();
		} else if (type instanceof BooleanType) {
			encodeBooleanTypeAccess();
		} else if (type instanceof ByteType) {
			encodeByteTypeAccess();
		} else if (type instanceof ShortType) {
			encodeShortTypeAccess();
		} else if (type instanceof IntType) {
			encodeIntTypeAccess();
		} else if (type instanceof LongType) {
			encodeLongTypeAccess();
		} else if (type instanceof DoubleType) {
			encodeDoubleTypeAccess();
		} else if (type instanceof FloatType) {
			encodeFloatTypeAccess();
		} else {
			throw new IllegalArgumentException("Unable to encode type " + type);
		}
	}

	default void encodeTypeSignature(final Type type) {
		if (type instanceof ClassType classType) {
			encode(classType.getFullyQualifiedName());
		} else if (type instanceof ArrayType arrayType) {
			encodeTypeSignature(arrayType.getBaseType());
			encode("~");
		} else {
			encode(type.toString());
		}
	}

}
