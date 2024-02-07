package byteback.encoder.boogie;

import byteback.encoder.Encoder;

public interface BplBinaryOperationEncoder extends Encoder<BplContext> {

	default void encodeBinaryOperation(final String operation) {
		encodeSpace();
		encode(operation);
		encodeSpace();
	}

}
