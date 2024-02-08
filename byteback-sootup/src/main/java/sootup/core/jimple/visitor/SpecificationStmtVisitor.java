package sootup.core.jimple.visitor;

import sootup.core.jimple.stmt.byteback.JAssertStmt;
import sootup.core.jimple.stmt.byteback.JInvariantStmt;

public interface SpecificationStmtVisitor extends StmtVisitor {

	void caseAssertStmt(JAssertStmt assertStmt);

	void caseInvariantStmt(JInvariantStmt invariantStmt);

}
