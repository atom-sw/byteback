package sootup.core.jimple.stmt;

import sootup.core.jimple.basic.JimpleComparator;
import sootup.core.jimple.basic.SpecComparator;
import sootup.core.jimple.basic.StmtPositionInfo;
import sootup.core.jimple.expr.SpecExpr;
import sootup.core.jimple.visitor.SpecStmtVisitor;
import sootup.core.util.printer.StmtPrinter;

public class JInvariantStmt extends SpecStmt {

	public JInvariantStmt(final SpecExpr specification, final StmtPositionInfo positionInfo) {
		super(specification, positionInfo);
	}

	@Override
	public void accept(final SpecStmtVisitor visitor) {
		visitor.caseInvariantStmt(this);
	}

	@Override
	public void toString(final StmtPrinter printer) {
		printer.literal("invariant ");;
		getSpecification().toString(printer);
	}

	@Override
	public int equivHashCode() {
		return 43 * getSpecification().equivHashCode();
	}

	@Override
	public boolean equivTo(final Object o, final SpecComparator comparator) {
		return comparator.caseInvariantStmt(o, this);
	}

}
