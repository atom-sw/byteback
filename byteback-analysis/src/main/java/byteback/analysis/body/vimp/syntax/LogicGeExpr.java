package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ExprSwitch;
import soot.jimple.GeExpr;
import soot.util.Switch;

/**
 * Logic equivalent of GeExpr.
 * @see soot.jimple.internal.JGeExpr
 *
 * @author paganma
 */
public class LogicGeExpr extends AbstractLogicBinopExpr implements GeExpr {

	public LogicGeExpr(final Value op1, final Value op2) {
		super(op1, op2);
	}

	public LogicGeExpr(final ValueBox op1box, final ValueBox op2box) {
		super(op1box, op2box);
	}

	@Override
	public String getSymbol() {
		return " >= ";
	}

	@Override
	public LogicGeExpr clone() {
		return new LogicGeExpr(Vimp.v().cloneIfNecessary(getOp1()), Vimp.v().cloneIfNecessary(getOp2()));
	}

	@Override
	public void apply(final Switch visitor) {
		if (visitor instanceof ExprSwitch exprSwitch) {
			exprSwitch.caseGeExpr(this);
		}
	}

	@Override
	public int getPrecedence() {
		return 600;
	}

}
