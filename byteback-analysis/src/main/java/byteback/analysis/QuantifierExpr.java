package byteback.analysis;

import byteback.analysis.vimp.LogicExpr;
import java.util.Iterator;
import java.util.List;
import soot.Local;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.util.Chain;
import soot.util.HashChain;

public abstract class QuantifierExpr implements LogicExpr {

	private Chain<Local> locals;

	private Value value;

	public QuantifierExpr(final Chain<Local> locals, final Value value) {
		this.value = value;
		setFreeLocals(locals);
	}

	public Value getValue() {
		return value;
	}

	public void setValue(final Value value) {
		this.value = value;
	}

	public Chain<Local> getFreeLocals() {
		return locals;
	}

	public final void setFreeLocals(final Chain<Local> freeLocals) {
		if (freeLocals.isEmpty()) {
			throw new IllegalArgumentException("a Quantifier must have at least one free local");
		}

		this.locals = freeLocals;
	}

	protected Chain<Local> cloneFreeLocals() {
		final Chain<Local> locals = new HashChain<>();

		for (Local local : getFreeLocals()) {
			locals.add((Local) local.clone());
		}

		return locals;
	}

	protected abstract String getSymbol();

	@Override
	public void toString(final UnitPrinter up) {
		final Iterator<Local> freeIt = locals.iterator();
		up.literal("(");
		up.literal(getSymbol());
		up.literal(" ");

		while (freeIt.hasNext()) {
			final Local local = freeIt.next();
			up.type(local.getType());
			up.literal(" ");
			local.toString(up);

			if (freeIt.hasNext()) {
				up.literal(", ");
			}
		}

		up.literal(" :: ");
		value.toString(up);
		up.literal(")");
	}

	@Override
	public List<ValueBox> getUseBoxes() {
		return value.getUseBoxes();
	}

	@Override
	public boolean equivTo(final Object o) {
		if (o instanceof QuantifierExpr q) {
			return q.value.equals(q.value);
		}

		return true;
	}

	@Override
	public int equivHashCode() {
		int hashCode = 17 ^ getSymbol().hashCode();

		for (Local local : locals) {
			hashCode += local.equivHashCode();
		}

		return hashCode + (value.equivHashCode() * 101);
	}

	@Override
	public abstract Object clone();

}
