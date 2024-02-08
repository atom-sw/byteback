package sootup.core.jimple.stmt.byteback;

import sootup.core.jimple.basic.SpecificationComparator;
import sootup.core.jimple.basic.StmtPositionInfo;
import sootup.core.jimple.expr.byteback.JLogicExpr;
import sootup.core.jimple.visitor.SpecificationStmtVisitor;
import sootup.core.util.printer.StmtPrinter;

public class JAssertStmt extends SpecificationStmt {

	public JAssertStmt(final JLogicExpr<?> condition, final StmtPositionInfo positionInfo) {
		super(condition, positionInfo);
	}

	@Override
	public JAssertStmt withCondition(final JLogicExpr<?> condition) {
		return new JAssertStmt(condition, positionInfo);
	}

	@Override
	public void accept(final SpecificationStmtVisitor visitor) {
		visitor.caseAssertStmt(this);
	}

	@Override
	public void toString(final StmtPrinter printer) {
		printer.literal("assert ");;
		getCondition().toString(printer);
	}

	@Override
	public int equivHashCode() {
		return 42 * getCondition().equivHashCode();
	}

	@Override
	public boolean equivTo(final Object o, final SpecificationComparator comparator) {
		return comparator.caseAssertStmt(o, this);
	}

}
