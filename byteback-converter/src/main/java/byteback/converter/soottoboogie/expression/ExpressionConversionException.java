package byteback.converter.soottoboogie.expression;

import byteback.converter.soottoboogie.ConversionException;
import soot.Value;

public class ExpressionConversionException extends ConversionException {

	private final Value value;

	public ExpressionConversionException(final Value value, final Exception exception) {
		super(exception);
		this.value = value;
	}

	public ExpressionConversionException(final Value value, final String message) {
		super(message);
		this.value = value;
	}

	public ExpressionConversionException(final Value value) {
		this(value, "Failed to convert expression " + value);
	}

	public Value getValue() {
		return value;
	}

	@Override
	public String getMessage() {
		return "While converting expression " + value + ":\n" + super.getMessage();
	}

}
