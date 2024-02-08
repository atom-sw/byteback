package sootup.core.jimple.basic;

import sootup.core.jimple.stmt.JAssertStmt;
import sootup.core.jimple.stmt.JInvariantStmt;

public class SpecComparator extends JimpleComparator {

	public boolean caseAssertStmt(final Object o, final JAssertStmt assertStmt) {
		return (o instanceof JAssertStmt otherAssertStmt)
				&& assertStmt.getSpecification().equivTo(assertStmt.getSpecification());
	}

	public boolean caseInvariantStmt(final Object o, final JInvariantStmt invariantStmt) {
		return (o instanceof JInvariantStmt otherInvariantSmt)
				&& invariantStmt.getSpecification().equivTo(otherInvariantSmt.getSpecification());
	}

}
