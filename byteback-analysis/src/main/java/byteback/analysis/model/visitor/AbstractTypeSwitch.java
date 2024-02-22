package byteback.analysis.model.visitor;

import byteback.analysis.common.visitor.Visitor;
import soot.DoubleType;
import soot.FloatType;
import soot.PrimType;
import soot.Type;

public abstract class AbstractTypeSwitch<R> extends soot.TypeSwitch<R> implements Visitor<Type, R> {

	public void caseRealType(final PrimType type) {
		defaultCase(type);
	}

	@Override
	public void caseDoubleType(final DoubleType type) {
		caseRealType(type);
	}

	@Override
	public void caseFloatType(final FloatType type) {
		caseRealType(type);
	}

}
