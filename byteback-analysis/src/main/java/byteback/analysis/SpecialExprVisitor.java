package byteback.analysis;

import byteback.analysis.vimp.OldExpr;
import byteback.analysis.vimp.VoidConstant;
import soot.Value;

public interface SpecialExprVisitor<T> extends Visitor<Value, T> {

	default void caseVoidConstant(final VoidConstant v) {
		caseDefault(v);
	}

	default void caseOldExpr(final OldExpr v) {
		caseDefault(v);
	}

}
