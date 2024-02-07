package byteback.verifier.boogie;

import byteback.verifier.Message;

/**
 * This is a very simple preliminary implementation for a class representing a
 * message from the Boogie verifier. In our case the message given by Boogie is
 * assumed to be one (the entire output of the verifier), in the future we will
 * create more specific types (e.g. for verification errors).
 */
public class BoogieMessage implements Message {

	private final String message;

	public BoogieMessage(final String message) {
		this.message = message;
	}

	public String format() {
		return message;
	}

}
