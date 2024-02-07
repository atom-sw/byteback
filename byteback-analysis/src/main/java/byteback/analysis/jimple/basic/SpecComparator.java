package byteback.analysis.jimple.basic;

import byteback.analysis.jimple.stmt.JAssertStmt;
import byteback.analysis.jimple.stmt.JInvariantStmt;
import sootup.core.jimple.basic.JimpleComparator;

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
