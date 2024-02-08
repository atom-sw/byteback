package sootup.core.jimple.expr.byteback;

import java.util.List;
import sootup.core.jimple.basic.JimpleComparator;
import sootup.core.jimple.basic.Value;
import sootup.core.jimple.common.expr.Expr;
import sootup.core.jimple.visitor.ExprVisitor;
import sootup.core.jimple.visitor.CompoundExprVisitor;
import sootup.core.types.Type;
import sootup.core.util.printer.StmtPrinter;

import javax.annotation.Nonnull;

public class JCompoundExpr<T extends Value> implements Value, Expr {

	private T inner;

	public JCompoundExpr(final T inner) {
		this.inner = inner;
	}

	public T getInner() {
		return inner;
	}

	public void setInner(final T inner) {
		this.inner = inner;
	}

	@Nonnull
	@Override
	public Type getType() {
		return inner.getType();
	}

	@Nonnull
	@Override
	public List<Value> getUses() {
		return inner.getUses();
	}

	@Override
	public boolean equivTo(final Object o, @Nonnull final JimpleComparator comparator) {
		return inner.equivTo(comparator);
	}

	@Override
	public int equivHashCode() {
		return inner.equivHashCode();
	}

	@Override
	public void toString(final StmtPrinter printer) {
		printer.literal("(");
		inner.toString(printer);
		printer.literal(")");
	}

	@Override
	public void accept(@Nonnull final ExprVisitor visitor) {
		if (visitor instanceof CompoundExprVisitor compoundExprVisitor) {
			accept(compoundExprVisitor);
		} else {
			visitor.defaultCaseExpr(this);
		}
	}

	public void accept(final CompoundExprVisitor visitor) {
		visitor.caseCompoundExpr(this);
	}

}
