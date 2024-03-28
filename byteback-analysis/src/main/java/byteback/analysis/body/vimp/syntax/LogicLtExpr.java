package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ExprSwitch;
import soot.jimple.LtExpr;
import soot.util.Switch;

/**
 * Logic equivalent of LeExpr.
 * @see soot.jimple.internal.JLeExpr
 *
 * @author paganma
 */
public class LogicLtExpr extends AbstractLogicBinopExpr implements LtExpr {

	public LogicLtExpr(final Value op1, final Value op2) {
		super(op1, op2);
	}

	public LogicLtExpr(final ValueBox op1box, final ValueBox op2box) {
		super(op1box, op2box);
	}

	@Override
	public String getSymbol() {
		return " < ";
	}

	@Override
	public LogicLtExpr clone() {
		return new LogicLtExpr(Vimp.v().cloneIfNecessary(getOp1()), Vimp.v().cloneIfNecessary(getOp2()));
	}

	@Override
	public void apply(final Switch visitor) {
		if (visitor instanceof ExprSwitch exprSwitch) {
			exprSwitch.caseLtExpr(this);
		}
	}

	@Override
	public int getPrecedence() {
		return 600;
	}

}
