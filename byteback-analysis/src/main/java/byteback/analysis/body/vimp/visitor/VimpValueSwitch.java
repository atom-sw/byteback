package byteback.analysis.body.vimp.visitor;

import byteback.analysis.body.vimp.syntax.*;
import byteback.analysis.common.visitor.Visitor;
import soot.Value;

public interface VimpValueSwitch<T> extends Visitor<Value, T> {

	default void caseConjExpr(final ConjExpr conjExpr) {
		defaultCase(conjExpr);
	}

	default void caseDisjExpr(final DisjExpr disjExpr) {
		defaultCase(disjExpr);
	}

	default void caseLogicXorExpr(final LogicXorExpr logicXorExpr) {
		defaultCase(logicXorExpr);
	}

	default void caseLogicImpliesExpr(final ImpliesExpr impliesExpr) {
		defaultCase(impliesExpr);
	}

	default void caseLogicIffExpr(final IffExpr iffExpr) {
		defaultCase(iffExpr);
	}

	default void caseLogicForallExpr(final ForallExpr forallExpr) {
		defaultCase(forallExpr);
	}

	default void caseLogicExistsExpr(final ExistsExpr existsExpr) {
		defaultCase(existsExpr);
	}

	default void caseNotExpr(final NotExpr notExpr) {
		defaultCase(notExpr);
	}

	default void caseLogicConstant(final LogicConstant logicConstant) {
		defaultCase(logicConstant);
	}

	default void caseCallExpr(final CallExpr callExpr) {
		defaultCase(callExpr);
	}

	default void caseNestedExpr(final NestedExpr nestedExpr) {
		defaultCase(nestedExpr);
	}

}
