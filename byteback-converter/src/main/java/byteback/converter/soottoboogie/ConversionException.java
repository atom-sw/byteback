package byteback.converter.soottoboogie;

/**
 * Base exception that may concern the conversion process between Jimple and
 * Boogie.
 */
public class ConversionException extends RuntimeException {

	public ConversionException(final String message) {
		super(message);
	}

	public ConversionException(final Exception exception) {
		super(exception);
	}

}
