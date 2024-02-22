package byteback.analysis.body.vimp.visitor;

import byteback.analysis.common.visitor.Visitor;
import byteback.analysis.body.vimp.OldExpr;
import byteback.analysis.body.vimp.VoidConstant;
import soot.Value;

public interface SpecialExprVisitor<T> extends Visitor<Value, T> {

	default void caseVoidConstant(final VoidConstant v) {
		defaultCase(v);
	}

	default void caseOldExpr(final OldExpr v) {
		defaultCase(v);
	}

}
