package byteback.encoder.boogie;

import byteback.encoder.common.Encoder;

public interface BplBinaryOperationEncoder extends Encoder<BplContext> {

	default void encodeBinaryOperation(final String operation) {
		encodeSpace();
		encode(operation);
		encodeSpace();
	}

}
