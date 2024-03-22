package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import soot.Value;
import soot.util.Switch;

public class IffExpr extends AbstractLogicBinopExpr {

	public IffExpr(final Value op1, final Value op2) {
		super(op1, op2);
	}

	@Override
	public String getSymbol() {
		return " ↔ ";
	}

	@Override
	public IffExpr clone() {
		return new IffExpr(Vimp.cloneIfNecessary(getOp1()), Vimp.cloneIfNecessary(getOp2()));
	}

	@Override
	public void apply(final Switch visitor) {
		if (visitor instanceof VimpValueSwitch<?> vimpValueSwitch) {
			vimpValueSwitch.caseLogicIffExpr(this);
		}
	}

	@Override
	public int getPrecedence() {
		return 500;
	}

}
