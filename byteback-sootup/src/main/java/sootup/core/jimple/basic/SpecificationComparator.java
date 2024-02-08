package sootup.core.jimple.basic;

import sootup.core.jimple.stmt.byteback.JAssertStmt;
import sootup.core.jimple.stmt.byteback.JInvariantStmt;

public class SpecificationComparator extends JimpleComparator {

	public boolean caseAssertStmt(final Object o, final JAssertStmt assertStmt) {
		return (o instanceof JAssertStmt otherAssertStmt)
				&& assertStmt.getCondition().equivTo(assertStmt.getCondition());
	}

	public boolean caseInvariantStmt(final Object o, final JInvariantStmt invariantStmt) {
		return (o instanceof JInvariantStmt otherInvariantSmt)
				&& invariantStmt.getCondition().equivTo(otherInvariantSmt.getCondition());
	}

}
