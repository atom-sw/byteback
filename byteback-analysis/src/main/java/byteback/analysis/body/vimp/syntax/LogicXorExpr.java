package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import soot.Value;
import soot.ValueBox;
import soot.jimple.XorExpr;

/**
 * Logic equivalent of LeExpr.
 * @see soot.jimple.internal.JLeExpr
 *
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
		return new LogicXorExpr(Vimp.v().cloneIfNecessary(getOp1()), Vimp.v().cloneIfNecessary(getOp2()));
	}

	@Override
	public int getPrecedence() {
		return 500;
	}

}
