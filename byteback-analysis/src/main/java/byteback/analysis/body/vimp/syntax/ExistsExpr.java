package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import soot.Local;
import soot.Value;
import soot.util.Chain;
import soot.util.Switch;

public class ExistsExpr extends QuantifierExpr {

	public ExistsExpr(final Chain<Local> freeLocals, final Value value) {
		super(freeLocals, value);
	}

	@Override
	protected String getSymbol() {
		return "∃";
	}

	@Override
	public void apply(final Switch visitor) {
		if (visitor instanceof VimpValueSwitch<?> vimpValueSwitch) {
			vimpValueSwitch.caseLogicExistsExpr(this);
		}
	}

	@Override
	public ExistsExpr clone() {
		return new ExistsExpr(cloneBindings(), Vimp.cloneIfNecessary(getValue()));
	}

}
