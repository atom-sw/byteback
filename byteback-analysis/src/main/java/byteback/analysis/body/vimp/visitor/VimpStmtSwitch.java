package byteback.analysis.body.vimp.visitor;

import byteback.analysis.body.vimp.syntax.AssertStmt;
import byteback.analysis.body.vimp.syntax.AssumeStmt;
import byteback.analysis.body.vimp.syntax.InvariantStmt;
import byteback.analysis.common.visitor.Visitor;
import byteback.analysis.body.jimple.syntax.Stmt;

public interface VimpStmtSwitch<T> extends Visitor<Stmt, T> {

    default void caseAssertionStmt(final AssertStmt assertStmt) {
        defaultCase(assertStmt);
    }

    default void caseAssumptionStmt(final AssumeStmt assumeStmt) {
        defaultCase(assumeStmt);
    }

    default void caseInvariantStmt(final InvariantStmt invariantStmt) {
        defaultCase(invariantStmt);
    }

}
