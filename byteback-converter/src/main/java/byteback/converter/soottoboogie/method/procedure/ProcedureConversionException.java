package byteback.converter.soottoboogie.method.procedure;

import byteback.converter.soottoboogie.ConversionException;
import soot.SootMethod;

public class ProcedureConversionException extends ConversionException {

	final SootMethod method;

	public ProcedureConversionException(final SootMethod method, final String message) {
		super(message);
		this.method = method;
	}

	public ProcedureConversionException(final SootMethod method, final Exception exception) {
		super(exception);
		this.method = method;
	}

	@Override
	public String getMessage() {
		return "While converting procedure method " + method.getSignature() + ":\n" + super.getMessage();
	}

}
