package byteback.analysis.body.vimp.visitor;

import byteback.analysis.body.vimp.*;
import byteback.analysis.common.visitor.Visitor;
import soot.Value;

public interface VimpValueSwitch<T> extends Visitor<Value, T> {

	default void caseLogicAndExpr(final LogicAndExpr v) {
		defaultCase(v);
	}

	default void caseLogicOrExpr(final LogicOrExpr v) {
		defaultCase(v);
	}

	default void caseLogicXorExpr(final LogicXorExpr v) {
		defaultCase(v);
	}

	default void caseLogicImpliesExpr(final LogicImpliesExpr v) {
		defaultCase(v);
	}

	default void caseLogicIffExpr(final LogicIffExpr v) {
		defaultCase(v);
	}

	default void caseLogicForallExpr(final LogicForallExpr v) {
		defaultCase(v);
	}

	default void caseLogicExistsExpr(final LogicExistsExpr v) {
		defaultCase(v);
	}

	default void caseLogicNotExpr(final LogicNotExpr v) {
		defaultCase(v);
	}

	default void caseLogicConstant(final LogicConstant v) {
		defaultCase(v);
	}

}
