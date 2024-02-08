package sootup.core.jimple.visitor;

import sootup.core.jimple.expr.CompoundExpr;
import sootup.core.jimple.visitor.StmtVisitor;

public interface SpecExprVisitor extends StmtVisitor {

	void caseCompoundExpr(CompoundExpr compoundExpr);

}
