package byteback.analysis;

import byteback.analysis.vimp.AssertionStmt;
import byteback.analysis.vimp.AssumptionStmt;
import byteback.analysis.vimp.InvariantStmt;
import soot.Unit;

public interface LogicStmtVisitor<T> extends Visitor<Unit, T> {

	default void caseAssertionStmt(final AssertionStmt s) {
		caseDefault(s);
	}

	default void caseAssumptionStmt(final AssumptionStmt s) {
		caseDefault(s);
	}

	default void caseInvariantStmt(final InvariantStmt s) {
		caseDefault(s);
	}

	default void caseDefault(final Unit s) {
	}

}
