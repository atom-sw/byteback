package byteback.encoder.boogie;

import byteback.encoder.common.Encoder;

public interface BplConstEncoder extends Encoder<BplContext> {

	default void encodeConst() {
		encode(BplSyntax.CONST);
		encodeSpace();
	}

}
