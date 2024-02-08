package sootup.core.jimple.stmt;

import sootup.core.jimple.basic.JimpleComparator;
import sootup.core.jimple.basic.SpecComparator;
import sootup.core.jimple.basic.StmtPositionInfo;
import sootup.core.jimple.expr.SpecExpr;
import sootup.core.jimple.visitor.SpecStmtVisitor;
import sootup.core.jimple.visitor.StmtVisitor;
import sootup.core.util.printer.StmtPrinter;

public class JAssertStmt extends SpecStmt {

	public JAssertStmt(final SpecExpr specification, final StmtPositionInfo positionInfo) {
		super(specification, positionInfo);
	}

	@Override
	public void accept(final StmtVisitor visitor) {
		if (visitor instanceof SpecStmtVisitor specStmtVisitor) {
			specStmtVisitor.caseAssertStmt(this);
		}
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
	public boolean equivTo(final Object o, JimpleComparator comparator) {
		if (comparator instanceof SpecComparator specComparator) {
			return specComparator.caseAssertStmt(o, this);
		}

		return false;
	}

}
