package byteback.analysis.body.vimp.syntax;

import soot.Local;
import soot.Value;
import soot.jimple.Jimple;
import soot.util.Chain;

/**
 * Existential quantification expression.
 *
 * @author paganma
 */
public class ExistsExpr extends QuantifierExpr implements UnswitchableExpr {

	public ExistsExpr(final Chain<Local> freeLocals, final Value value) {
		super(freeLocals, value);
	}

	@Override
	protected String getSymbol() {
		return "∃";
	}

	@Override
	public ExistsExpr clone() {
		return new ExistsExpr(cloneBindings(), Jimple.cloneIfNecessary(getValue()));
	}

}
