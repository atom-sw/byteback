package byteback.analysis.body.vimp.visitor;

import byteback.analysis.body.jimple.visitor.AbstractJimpleValueSwitch;
import byteback.analysis.body.vimp.syntax.LogicAndExpr;
import byteback.analysis.body.vimp.syntax.LogicOrExpr;
import byteback.analysis.body.vimp.syntax.LogicXorExpr;
import byteback.analysis.body.vimp.syntax.SpecialExprSwitch;

public abstract class AbstractVimpValueSwitch<R> extends AbstractJimpleValueSwitch<R>
        implements LogicExprSwitch<R>, SpecialExprSwitch<R> {

    @Override
    public void caseLogicAndExpr(final LogicAndExpr logicAndExpr) {
        caseAndExpr(logicAndExpr);
    }

    @Override
    public void caseLogicOrExpr(final LogicOrExpr logicOrExpr) {
        caseOrExpr(logicOrExpr);
    }

    @Override
    public void caseLogicXorExpr(final LogicXorExpr logicXorExpr) {
        caseXorExpr(logicXorExpr);
    }

}
