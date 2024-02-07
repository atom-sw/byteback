package byteback.analysis.jimple.expr;

import byteback.analysis.jimple.visitor.SpecExprVisitor;
import java.util.List;
import sootup.core.jimple.basic.Immediate;
import sootup.core.jimple.basic.JimpleComparator;
import sootup.core.jimple.basic.Value;
import sootup.core.jimple.common.expr.Expr;
import sootup.core.jimple.visitor.ExprVisitor;
import sootup.core.types.Type;
import sootup.core.util.printer.StmtPrinter;

public class CompoundExpr implements Expr, Immediate {

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
		wrapped.toString(printer);
	}

	@Override
	public void accept(final ExprVisitor visitor) {
		if (visitor instanceof SpecExprVisitor specExprVisitor) {
			specExprVisitor.caseCompoundExpr(this);
		}
	}

}
