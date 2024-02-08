package sootup.core.jimple.expr;

import java.util.List;
import sootup.core.jimple.basic.JimpleComparator;
import sootup.core.jimple.basic.Value;
import sootup.core.jimple.common.expr.Expr;
import sootup.core.jimple.visitor.SpecExprVisitor;
import sootup.core.types.Type;
import sootup.core.util.printer.StmtPrinter;

public class CompoundExpr extends SpecExpr {

	private final Expr wrapped;

	public CompoundExpr(final Expr wrapped) {
		this.wrapped = wrapped;
	}

	public Expr getWrapped() {
		return wrapped;
	}

	@Override
	public Type getType() {
		return wrapped.getType();
	}

	@Override
	public List<Value> getUses() {
		return wrapped.getUses();
	}

	@Override
	public boolean equivTo(final Object o, final JimpleComparator comparator) {
		return wrapped.equivTo(comparator);
	}

	@Override
	public int equivHashCode() {
		return wrapped.equivHashCode();
	}

	@Override
	public void toString(final StmtPrinter printer) {
		printer.literal("(");
		wrapped.toString(printer);
		printer.literal(")");
	}

	@Override
	public void accept(final SpecExprVisitor visitor) {
		visitor.caseCompoundExpr(this);
	}

}
