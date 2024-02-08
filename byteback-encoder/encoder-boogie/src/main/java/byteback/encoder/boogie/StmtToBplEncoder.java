package byteback.encoder.boogie;

import sootup.core.jimple.basic.Local;
import sootup.core.jimple.basic.Value;
import sootup.core.jimple.common.expr.AbstractInvokeExpr;
import sootup.core.jimple.common.ref.JArrayRef;
import sootup.core.jimple.common.ref.JFieldRef;
import sootup.core.jimple.common.stmt.JAssignStmt;
import sootup.core.jimple.common.stmt.Stmt;

public interface StmtToBplEncoder {

	public default void encodeLocalAssignment(final Local assignee, final Value assigned) {
	}

	public default void encodeArrayAssignment(final JArrayRef assignee, final Value assigned) {
	}

	public default void encodeFieldAssignment(final JFieldRef assignee, final Value assigned) {
	}

	public default void encodeCall(final Local assignee, final AbstractInvokeExpr invokeExpr) {
	}

	public default void encodeAssignStmt(final JAssignStmt<?, ?> assignStmt) {
		final Value assigneeValue = assignStmt.getLeftOp();
		final Value assignedValue = assignStmt.getRightOp();
		if (assignedValue instanceof AbstractInvokeExpr invokeExpr) {
			if (assigneeValue instanceof Local localAssignee) {
				encodeCall(localAssignee, invokeExpr);
			} else {
				throw new IllegalStateException("Unable to encode invoke expression assigned to " + assigneeValue);
			}
		}
		if (assigneeValue instanceof Local localAssignee) {
			encodeLocalAssignment(localAssignee, assignedValue);
		} else if (assigneeValue instanceof JArrayRef arrayAssignee) {
			encodeArrayAssignment(arrayAssignee, assignedValue);
		} else if (assigneeValue instanceof JFieldRef fieldAssignee) {
			encodeFieldAssignment(fieldAssignee, assignedValue);
		}
	}

	public default void encodeStmt(final Stmt stmt) {
		if (stmt instanceof JAssignStmt assignStmt) {
			encodeAssignStmt(assignStmt);
		}
	}

	public default void encodeStmts(final Iterable<Stmt> stmts) {
		for (final Stmt stmt : stmts) {
			encodeStmt(stmt);
		}
	}

}
