package byteback.encoder.boogie;

import byteback.encoder.Encoder;

public interface BplProcedureEncoder extends Encoder<BplContext> {

	public default void encodeProcedure() {
		encode(BplSyntax.PROCEDURE);
		encodeSpace();
	}

}
