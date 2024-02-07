package byteback.analysis.jimple.visitor;

import byteback.analysis.jimple.stmt.JAssertStmt;
import byteback.analysis.jimple.stmt.JInvariantStmt;
import sootup.core.jimple.visitor.StmtVisitor;

public interface SpecStmtVisitor extends StmtVisitor {

	void caseAssertStmt(JAssertStmt assertStmt);

	void caseInvariantStmt(JInvariantStmt invariantStmt);

}
