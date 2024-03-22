package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import soot.Value;
import soot.ValueBox;
import soot.jimple.XorExpr;
import soot.util.Switch;

/**
 * Logic equivalent of LeExpr.
 * @see soot.jimple.internal.JLeExpr
 * @author paganma
 */
public class LogicXorExpr extends AbstractLogicBinopExpr implements XorExpr {

	public LogicXorExpr(final Value op1, final Value op2) {
		super(op1, op2);
	}

	public LogicXorExpr(final ValueBox op1box, final ValueBox op2box) {
		super(op1box, op2box);
	}

	@Override
	public String getSymbol() {
		return " ⊕ ";
	}

	@Override
	public LogicXorExpr clone() {
		return new LogicXorExpr(Vimp.cloneIfNecessary(getOp1()), Vimp.cloneIfNecessary(getOp2()));
	}

	@Override
	public void apply(final Switch visitor) {
		if (visitor instanceof VimpValueSwitch<?> vimpValueSwitch) {
			vimpValueSwitch.caseLogicXorExpr(this);
		}
	}

	@Override
	public int getPrecedence() {
		return 500;
	}

}
