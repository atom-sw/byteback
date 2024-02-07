package byteback.encoder.boogie;

import byteback.encoder.Encoder;

public interface BplIdentifierEncoder extends Encoder<BplContext> {
	public default void encodeIdentifier(final String name) {
		encode("`" + name + "`");
	}
}
