package sootup.core.jimple.visitor;

import sootup.core.jimple.stmt.JAssertStmt;
import sootup.core.jimple.stmt.JInvariantStmt;
import sootup.core.jimple.visitor.StmtVisitor;

public interface SpecStmtVisitor extends StmtVisitor {

	void caseAssertStmt(JAssertStmt assertStmt);

	void caseInvariantStmt(JInvariantStmt invariantStmt);

}
