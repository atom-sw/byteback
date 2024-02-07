package byteback.analysis.jimple.visitor;

import byteback.analysis.jimple.expr.CompoundExpr;
import sootup.core.jimple.visitor.StmtVisitor;

public interface SpecExprVisitor extends StmtVisitor {

	void caseCompoundExpr(CompoundExpr compoundExpr);

}
