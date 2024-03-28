package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import soot.Local;
import soot.Value;
import soot.util.Chain;
import soot.util.Switch;

/**
 * Existential quantification expression.
 *
 * @author paganma
 */
public class ExistsExpr extends QuantifierExpr {

	public ExistsExpr(final Chain<Local> freeLocals, final Value value) {
		super(freeLocals, value);
	}

	@Override
	protected String getSymbol() {
		return "∃";
	}

	@Override
	public ExistsExpr clone() {
		return new ExistsExpr(cloneBindings(), Vimp.v().cloneIfNecessary(getValue()));
	}

}
