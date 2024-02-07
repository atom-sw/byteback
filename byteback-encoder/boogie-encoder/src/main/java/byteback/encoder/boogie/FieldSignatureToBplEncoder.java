package byteback.encoder.boogie;

import byteback.encoder.Encoder;
import sootup.core.signatures.FieldSignature;

public interface FieldSignatureToBplEncoder extends Encoder<BplContext> {

	public default void encodeField(final FieldSignature fieldSignature) {
	}

}
