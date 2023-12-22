package byteback.converter.soottoboogie.method.function;

import byteback.converter.soottoboogie.ConversionException;
import soot.SootMethod;

public class FunctionConversionException extends ConversionException {

	final SootMethod method;

	public FunctionConversionException(final SootMethod method, final String message) {
		super(message);
		this.method = method;
	}

	public FunctionConversionException(final SootMethod method, final Exception exception) {
		super(exception);
		this.method = method;
	}

	@Override
	public String getMessage() {

		return "Exception while converting pure method " + method.getSignature() + ":\n" + super.getMessage();
	}

}
