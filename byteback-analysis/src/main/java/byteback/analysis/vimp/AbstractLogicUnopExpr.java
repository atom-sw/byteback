package byteback.analysis.vimp;

import byteback.analysis.Vimp;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.jimple.internal.AbstractUnopExpr;

public abstract class AbstractLogicUnopExpr extends AbstractUnopExpr {

	public AbstractLogicUnopExpr(final Value v) {
		super(Vimp.v().newArgBox(v));
	}

	public AbstractLogicUnopExpr(final ValueBox vbox) {
		super(vbox);
	}

	public abstract String getSymbol();

	@Override
	public void toString(final UnitPrinter up) {
		up.literal(getSymbol());
		getOp().toString(up);
	}

	@Override
	public int equivHashCode() {
		return getOp().equivHashCode() * 101 + 17 ^ getSymbol().hashCode();
	}

	@Override
	public boolean equivTo(final Object o) {
		if (o instanceof AbstractLogicUnopExpr v) {
			return getSymbol().equals(v.getSymbol()) && getOp().equivTo(v.getOp());
		}

		return false;
	}

}
