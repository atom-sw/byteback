package sootup.core.jimple.stmt.byteback;

import sootup.core.jimple.basic.JimpleComparator;
import sootup.core.jimple.basic.SpecificationComparator;
import sootup.core.jimple.basic.StmtPositionInfo;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.jimple.expr.byteback.JLogicExpr;
import sootup.core.jimple.visitor.SpecificationStmtVisitor;
import sootup.core.jimple.visitor.StmtVisitor;

import javax.annotation.Nonnull;

public abstract class SpecificationStmt extends Stmt {

	private final JLogicExpr<?> condition;

	public SpecificationStmt(final JLogicExpr<?> condition, final StmtPositionInfo positionInfo) {
		super(positionInfo);
		this.condition = condition;
	}

	public JLogicExpr<?> getCondition() {
		return condition;
	}

	public abstract SpecificationStmt withCondition(final JLogicExpr<?> specification);

	public abstract void accept(final SpecificationStmtVisitor visitor);

	public abstract boolean equivTo(final Object o, final SpecificationComparator comparator);

	@Override
	public boolean equivTo(final Object o, @Nonnull final JimpleComparator comparator) {
		if (comparator instanceof SpecificationComparator specificationComparator) {
			return equivTo(o, comparator);
		}
		return false;
	}

	@Override
	public void accept(@Nonnull final StmtVisitor visitor) {
		if (visitor instanceof SpecificationStmtVisitor specExprVisitor) {
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
