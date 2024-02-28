package byteback.analysis.body.vimp.visitor;

import byteback.analysis.body.jimple.visitor.AbstractJimpleValueSwitch;
import byteback.analysis.body.vimp.SpecialExprSwitch;
import byteback.analysis.body.vimp.LogicAndExpr;
import byteback.analysis.body.vimp.LogicOrExpr;
import byteback.analysis.body.vimp.LogicXorExpr;
import soot.Value;

public abstract class AbstractVimpValueSwitch<R> extends AbstractJimpleValueSwitch<R>
		implements LogicExprSwitch<R>, SpecialExprSwitch<R> {

	@Override
	public void caseLogicAndExpr(final LogicAndExpr v) {
		caseAndExpr(v);
	}

	@Override
	public void caseLogicOrExpr(final LogicOrExpr v) {
		caseOrExpr(v);
	}

	@Override
	public void caseLogicXorExpr(final LogicXorExpr v) {
		caseXorExpr(v);
	}

}
