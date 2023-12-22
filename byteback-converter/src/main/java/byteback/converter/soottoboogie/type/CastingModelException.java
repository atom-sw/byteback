package byteback.converter.soottoboogie.type;

import byteback.converter.soottoboogie.ConversionException;
import soot.Type;

public class CastingModelException extends ConversionException {

	public final Type fromType;
	public final Type toType;

	public CastingModelException(final Type fromType, final Type toType) {
		super("Casting from " + fromType + " to " + toType + " is not supported");
		this.fromType = fromType;
		this.toType = toType;
	}

}
