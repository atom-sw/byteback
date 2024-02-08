package byteback.encoder.boogie;

import byteback.encoder.common.Encoder;

public interface BplIdentifierEncoder extends Encoder<BplContext> {
	public default void encodeIdentifier(final String name) {
		encode("`" + name + "`");
	}
}
