package sootup.core.jimple.stmt;

import sootup.core.jimple.basic.SpecComparator;
import sootup.core.jimple.basic.StmtPositionInfo;
import sootup.core.jimple.expr.SpecExpr;
import sootup.core.jimple.visitor.SpecStmtVisitor;
import sootup.core.util.printer.StmtPrinter;

public class JAssertStmt extends SpecStmt {

	public JAssertStmt(final SpecExpr specification, final StmtPositionInfo positionInfo) {
		super(specification, positionInfo);
	}

	@Override
	public void accept(final SpecStmtVisitor visitor) {
		visitor.caseAssertStmt(this);
	}

	@Override
	public void toString(final StmtPrinter printer) {
		printer.literal("assert ");;
		getSpecification().toString(printer);
	}

	@Override
	public int equivHashCode() {
		return 42 * getSpecification().equivHashCode();
	}

	@Override
	public boolean equivTo(final Object o, final SpecComparator comparator) {
		return comparator.caseAssertStmt(o, this);
	}

}
