package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ExprSwitch;
import soot.jimple.GtExpr;
import soot.util.Switch;

/**
 * Logic equivalent of GtExpr.
 * @see soot.jimple.internal.JGtExpr
 *
 * @author paganma
 */
public class LogicGtExpr extends AbstractLogicBinopExpr implements GtExpr {

	public LogicGtExpr(final Value op1, final Value op2) {
		super(op1, op2);
	}

	public LogicGtExpr(final ValueBox op1box, final ValueBox op2box) {
		super(op1box, op2box);
	}

	@Override
	public String getSymbol() {
		return " > ";
	}

	@Override
	public LogicGtExpr clone() {
		return new LogicGtExpr(Vimp.v().cloneIfNecessary(getOp1()), Vimp.v().cloneIfNecessary(getOp2()));
	}

	@Override
	public void apply(final Switch visitor) {
		if (visitor instanceof ExprSwitch exprSwitch) {
			exprSwitch.caseGtExpr(this);
		}
	}

	@Override
	public int getPrecedence() {
		return 600;
	}

}
