package byteback.analysis.body.vimp.visitor;

import byteback.analysis.body.vimp.AssertionStmt;
import byteback.analysis.body.vimp.AssumptionStmt;
import byteback.analysis.body.vimp.InvariantStmt;
import byteback.analysis.common.visitor.Visitor;
import soot.Unit;
import soot.jimple.Stmt;

public interface VimpStmtSwitch<T> extends Visitor<Stmt, T> {

	default void caseAssertionStmt(final AssertionStmt s) {
		defaultCase(s);
	}

	default void caseAssumptionStmt(final AssumptionStmt s) {
		defaultCase(s);
	}

	default void caseInvariantStmt(final InvariantStmt s) {
		defaultCase(s);
	}

}
