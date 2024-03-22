package byteback.analysis.body.vimp.visitor;

import byteback.analysis.body.jimple.visitor.AbstractJimpleValueSwitch;
import byteback.analysis.body.vimp.syntax.ConjExpr;
import byteback.analysis.body.vimp.syntax.DisjExpr;
import byteback.analysis.body.vimp.syntax.LogicXorExpr;

public abstract class AbstractVimpValueSwitch<R> extends AbstractJimpleValueSwitch<R>
		implements LogicExprSwitch<R>, soot.jimple.ExprSwitch, byteback.analysis.body.vimp.visitor.SpecialExprSwitch<R> {

	@Override
	public void caseConjExpr(final ConjExpr conjExpr) {
		caseAndExpr(conjExpr);
	}

	@Override
	public void caseDisjExpr(final DisjExpr disjExpr) {
		caseOrExpr(disjExpr);
	}

	@Override
	public void caseLogicXorExpr(final LogicXorExpr logicXorExpr) {
		caseXorExpr(logicXorExpr);
	}

}
