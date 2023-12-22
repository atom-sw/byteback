package byteback.analysis;

import byteback.analysis.util.SootTypes;
import soot.DoubleType;
import soot.FloatType;
import soot.PrimType;
import soot.Type;

/**
 * Base class for a {@link SootTypes} visitor.
 */
public abstract class TypeSwitch<R> extends soot.TypeSwitch<R> implements Visitor<Type, R> {

	public void caseRealType(final PrimType type) {
		caseDefault(type);
	}

	@Override
	public void caseDoubleType(final DoubleType type) {
		caseRealType(type);
	}

	@Override
	public void caseFloatType(final FloatType type) {
		caseRealType(type);
	}

	@Override
	public void caseDefault(Type type) {
	}

	@Override
	public void defaultCase(Type type) {
		caseDefault(type);
	}

}
