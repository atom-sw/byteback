package byteback.analysis;

import byteback.analysis.vimp.LogicAndExpr;
import byteback.analysis.vimp.LogicConstant;
import byteback.analysis.vimp.LogicExistsExpr;
import byteback.analysis.vimp.LogicForallExpr;
import byteback.analysis.vimp.LogicIffExpr;
import byteback.analysis.vimp.LogicImpliesExpr;
import byteback.analysis.vimp.LogicNotExpr;
import byteback.analysis.vimp.LogicOrExpr;
import byteback.analysis.vimp.LogicXorExpr;
import soot.Value;

public interface LogicExprVisitor<T> extends Visitor<Value, T> {

	default void caseLogicAndExpr(final LogicAndExpr v) {
		caseDefault(v);
	}

	default void caseLogicOrExpr(final LogicOrExpr v) {
		caseDefault(v);
	}

	default void caseLogicXorExpr(final LogicXorExpr v) {
		caseDefault(v);
	}

	default void caseLogicImpliesExpr(final LogicImpliesExpr v) {
		caseDefault(v);
	}

	default void caseLogicIffExpr(final LogicIffExpr v) {
		caseDefault(v);
	}

	default void caseLogicForallExpr(final LogicForallExpr v) {
		caseDefault(v);
	}

	default void caseLogicExistsExpr(final LogicExistsExpr v) {
		caseDefault(v);
	}

	default void caseLogicNotExpr(final LogicNotExpr v) {
		caseDefault(v);
	}

	default void caseLogicConstant(final LogicConstant v) {
		caseDefault(v);
	}

	default void caseDefault(final Value v) {
	}

}
