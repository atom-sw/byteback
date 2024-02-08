package byteback.encoder.boogie;

import byteback.encoder.common.Encoder;

public interface BplAxiomEncoder extends Encoder<BplContext> {

	default void encodeAxiom() {
		encode(BplSyntax.AXIOM);
		encodeSpace();
	}

}
