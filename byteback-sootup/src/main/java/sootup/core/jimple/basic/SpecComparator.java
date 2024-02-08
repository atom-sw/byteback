package sootup.core.jimple.basic;

import sootup.core.jimple.basic.JimpleComparator;
import sootup.core.jimple.stmt.JAssertStmt;
import sootup.core.jimple.stmt.JInvariantStmt;

public class SpecComparator extends JimpleComparator {

	public boolean caseAssertStmt(final Object o, final JAssertStmt assertStmt) {
		if (o instanceof JAssertStmt otherAssertStmt) {
		}

		return false;
	}

	public boolean caseInvariantStmt(final Object o, final JInvariantStmt invariantStmt) {
		if (o instanceof JAssertStmt otherAssertStmt) {
		}

		return false;
	}

}
