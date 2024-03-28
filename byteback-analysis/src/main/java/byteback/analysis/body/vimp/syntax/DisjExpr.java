package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.transformer.VimpValueBodyTransformer;
import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import soot.Value;
import soot.ValueBox;
import soot.jimple.OrExpr;
import soot.util.Switch;

/**
 * A logical disjunction expression. It replaces bitwise-and operations between booleans.
 * @see VimpValueBodyTransformer
 *
 * @author paganma
 */
public class DisjExpr extends AbstractLogicBinopExpr implements OrExpr {

	public DisjExpr(final Value op1, final Value op2) {
		super(op1, op2);
	}

	public DisjExpr(final ValueBox op1box, final ValueBox op2box) {
		super(op1box, op2box);
	}

	public String getSymbol() {
		return " ∨ ";
	}

	public DisjExpr clone() {
		return new DisjExpr(Vimp.v().cloneIfNecessary(getOp1()), Vimp.v().cloneIfNecessary(getOp2()));
	}

	@Override
	public void apply(final Switch visitor) {
		if (visitor instanceof VimpValueSwitch<?> vimpValueSwitch) {
			vimpValueSwitch.caseDisjExpr(this);
		}
	}

	@Override
	public int getPrecedence() {
		return 500;
	}

}
