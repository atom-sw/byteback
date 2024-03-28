package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.jimple.internal.AbstractUnopExpr;

/**
 * Base class for a logic unary expression.
 *
 * @author paganma
 */
public abstract class AbstractLogicUnopExpr extends AbstractUnopExpr {

	public AbstractLogicUnopExpr(final Value op) {
		super(Vimp.v().newArgBox(op));
	}

	public AbstractLogicUnopExpr(final ValueBox opBox) {
		super(opBox);
	}

	public abstract String getSymbol();

	@Override
	public boolean equivTo(final Object object) {
		if (object instanceof AbstractLogicUnopExpr logicUnopExpr) {
			return getSymbol().equals(logicUnopExpr.getSymbol()) && getOp().equivTo(logicUnopExpr.getOp());
		}

		return false;
	}

	@Override
	public void toString(final UnitPrinter printer) {
		printer.literal(getSymbol());
		getOp().toString(printer);
	}

}
