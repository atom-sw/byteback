package sootup.core.jimple.stmt;

import sootup.core.jimple.basic.JimpleComparator;
import sootup.core.jimple.basic.SpecComparator;
import sootup.core.jimple.basic.StmtPositionInfo;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.jimple.expr.SpecExpr;
import sootup.core.jimple.visitor.SpecStmtVisitor;
import sootup.core.jimple.visitor.StmtVisitor;

public abstract class SpecStmt extends Stmt {

	private final SpecExpr specification;

	public SpecStmt(final SpecExpr specification, final StmtPositionInfo positionInfo) {
		super(positionInfo);
		this.specification = specification;
	}

	public SpecExpr getSpecification() {
		return specification;
	}

	public abstract void accept(final SpecStmtVisitor visitor);

	public abstract boolean equivTo(final Object o, final SpecComparator comparator);

	@Override
	public boolean equivTo(final Object o, final JimpleComparator comparator) {
		if (comparator instanceof SpecComparator specComparator) {
			return equivTo(o, comparator);
		}
		return false;
	}

	@Override
	public void accept(final StmtVisitor visitor) {
		if (visitor instanceof SpecStmtVisitor specExprVisitor) {
			accept(specExprVisitor);
		} else {
			visitor.defaultCaseStmt(this);
		}
	}

	@Override
	public boolean branches() {
		return false;
	}

	@Override
	public boolean fallsThrough() {
		return true;
	}

}
