package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import soot.Value;
import soot.util.Switch;

/**
 * Logical equivalence expression.
 *
 * @author paganma
 */
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
		return new IffExpr(Vimp.v().cloneIfNecessary(getOp1()), Vimp.v().cloneIfNecessary(getOp2()));
	}

	@Override
	public int getPrecedence() {
		return 500;
	}

}
