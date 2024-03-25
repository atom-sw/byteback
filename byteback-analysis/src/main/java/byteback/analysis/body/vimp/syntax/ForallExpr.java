package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import soot.Local;
import soot.Value;
import soot.util.Chain;
import soot.util.Switch;

/**
 * Universal quantifier expression.
 * @author paganma
 */
public class ForallExpr extends QuantifierExpr {

	public ForallExpr(final Chain<Local> freeLocals, final Value value) {
		super(freeLocals, value);
	}

	@Override
	protected String getSymbol() {
		return "∀";
	}

	@Override
	public void apply(final Switch visitor) {
		if (visitor instanceof VimpValueSwitch<?> vimpValueSwitch) {
			vimpValueSwitch.caseLogicForallExpr(this);
		}
	}

	@Override
	public ForallExpr clone() {
		return new ForallExpr(cloneBindings(), Vimp.v().cloneIfNecessary(getValue()));
	}

}
