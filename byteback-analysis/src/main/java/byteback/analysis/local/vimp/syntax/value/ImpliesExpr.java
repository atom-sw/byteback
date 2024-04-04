package byteback.analysis.local.vimp.syntax.value;

import byteback.analysis.local.common.syntax.value.DefaultCaseValue;
import soot.Value;
import soot.jimple.Jimple;
import soot.jimple.internal.AbstractIntBinopExpr;
import soot.jimple.internal.ImmediateBox;

/**
 * Boolean implication expression.
 *
 * @author paganma
 */
public class ImpliesExpr extends AbstractIntBinopExpr implements DefaultCaseValue {

	public ImpliesExpr(final Value op1, final Value op2) {
		super(new ImmediateBox(op1), new ImmediateBox(op2));
	}

	@Override
	public String getSymbol() {
		return " -> ";
	}

	@Override
	public ImpliesExpr clone() {
		return new ImpliesExpr(Jimple.cloneIfNecessary(getOp1()), Jimple.cloneIfNecessary(getOp2()));
	}

}
