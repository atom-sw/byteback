package sootup.core.jimple.visitor;

import sootup.core.jimple.expr.byteback.JCompoundExpr;
import sootup.core.jimple.expr.byteback.JLogicExpr;

public interface CompoundExprVisitor extends StmtVisitor {

	void caseCompoundExpr(JCompoundExpr<?> compoundExpr);

	void caseLogicExpr(JLogicExpr<?> logicExpr);

}
