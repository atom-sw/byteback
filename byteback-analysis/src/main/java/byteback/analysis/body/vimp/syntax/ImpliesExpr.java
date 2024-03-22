package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import soot.Value;
import soot.util.Switch;

/**
 * Boolean implication expression.
 * @author paganma
 */
public class ImpliesExpr extends AbstractLogicBinopExpr {

	public ImpliesExpr(final Value op1, final Value op2) {
		super(op1, op2);
	}

	@Override
	public String getSymbol() {
		return " → ";
	}

	@Override
	public ImpliesExpr clone() {
		return new ImpliesExpr(Vimp.cloneIfNecessary(getOp1()), Vimp.cloneIfNecessary(getOp2()));
	}

	@Override
	public void apply(final Switch visitor) {
		if (visitor instanceof VimpValueSwitch<?> vimpValueSwitch) {
			vimpValueSwitch.caseLogicImpliesExpr(this);
		}
	}

	@Override
	public int getPrecedence() {
		return 500;
	}

}
