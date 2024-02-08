package sootup.core.jimple.stmt.byteback;

import sootup.core.jimple.basic.SpecificationComparator;
import sootup.core.jimple.basic.StmtPositionInfo;
import sootup.core.jimple.expr.byteback.JLogicExpr;
import sootup.core.jimple.visitor.SpecificationStmtVisitor;
import sootup.core.util.printer.StmtPrinter;

public class JInvariantStmt extends SpecificationStmt {

	public JInvariantStmt(final JLogicExpr<?> condition, final StmtPositionInfo positionInfo) {
		super(condition, positionInfo);
	}

	@Override
	public JInvariantStmt withCondition(final JLogicExpr<?> specification) {
		return new JInvariantStmt(specification, positionInfo);
	}

	@Override
	public void accept(final SpecificationStmtVisitor visitor) {
		visitor.caseInvariantStmt(this);
	}

	@Override
	public void toString(final StmtPrinter printer) {
		printer.literal("invariant ");;
		getCondition().toString(printer);
	}

	@Override
	public int equivHashCode() {
		return 43 * getCondition().equivHashCode();
	}

	@Override
	public boolean equivTo(final Object o, final SpecificationComparator comparator) {
		return comparator.caseInvariantStmt(o, this);
	}

}
