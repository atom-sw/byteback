package byteback.converter.soottoboogie.type;

import byteback.analysis.TypeSwitch;
import byteback.converter.soottoboogie.ConversionException;
import byteback.converter.soottoboogie.Prelude;
import byteback.frontend.boogie.ast.TypeAccess;
import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.RefType;
import soot.ShortType;
import soot.Type;

public class TypeAccessExtractor extends TypeSwitch<TypeAccess> {

	private TypeAccess typeAccess;

	public void setTypeAccess(final TypeAccess typeAccess) {
		this.typeAccess = typeAccess;
	}

	@Override
	public void caseByteType(final ByteType byteType) {
		setTypeAccess(Prelude.v().getIntegerType().makeTypeAccess());
	}

	@Override
	public void caseShortType(final ShortType shortType) {
		setTypeAccess(Prelude.v().getIntegerType().makeTypeAccess());
	}

	@Override
	public void caseIntType(final IntType integerType) {
		setTypeAccess(Prelude.v().getIntegerType().makeTypeAccess());
	}

	@Override
	public void caseCharType(final CharType charType) {
		setTypeAccess(Prelude.v().getIntegerType().makeTypeAccess());
	}

	@Override
	public void caseLongType(final LongType longType) {
		setTypeAccess(Prelude.v().getIntegerType().makeTypeAccess());
	}

	@Override
	public void caseDoubleType(final DoubleType doubleType) {
		setTypeAccess(Prelude.v().getRealType().makeTypeAccess());
	}

	@Override
	public void caseFloatType(final FloatType floatType) {
		setTypeAccess(Prelude.v().getRealType().makeTypeAccess());
	}

	@Override
	public void caseBooleanType(final BooleanType booleanType) {
		setTypeAccess(Prelude.v().getBooleanType().makeTypeAccess());
	}

	@Override
	public void caseRefType(final RefType referenceType) {
		setTypeAccess(Prelude.v().getReferenceType().makeTypeAccess());
	}

	@Override
	public void caseArrayType(final ArrayType arrayType) {
		setTypeAccess(Prelude.v().getReferenceType().makeTypeAccess());
	}

	@Override
	public void caseDefault(final Type type) {
		throw new ConversionException("Cannot extract type access for type " + type);
	}

	@Override
	public TypeAccess result() {
		if (typeAccess == null) {
			throw new IllegalStateException("Could not retrieve type access");
		} else {
			return typeAccess;
		}
	}

}
