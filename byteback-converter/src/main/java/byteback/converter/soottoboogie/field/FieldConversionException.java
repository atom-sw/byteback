package byteback.converter.soottoboogie.field;

import byteback.converter.soottoboogie.ConversionException;
import soot.SootField;

public class FieldConversionException extends ConversionException {

	private final SootField field;

	public FieldConversionException(final SootField field, final String message) {
		super(message);
		this.field = field;
	}

	public FieldConversionException(final SootField field, final Exception exception) {
		super(exception);
		this.field = field;
	}

	@Override
	public String getMessage() {
		return "Exception while converting field " + field.getName() + ":\n" + super.getMessage();
	}

}
