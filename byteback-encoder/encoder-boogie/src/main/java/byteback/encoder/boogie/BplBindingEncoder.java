package byteback.encoder.boogie;

import byteback.encoder.common.Encoder;

public interface BplBindingEncoder extends Encoder<BplContext> {

	default void encodeTypeSeparator() {
		encode(BplSyntax.TYPE_SEPARATOR);
		encodeSpace();
	}

}
