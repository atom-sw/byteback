package byteback.analysis.vimp;

import byteback.analysis.Vimp;
import soot.Value;
import soot.ValueBox;
import soot.grimp.Precedence;
import soot.jimple.internal.AbstractBinopExpr;

public abstract class AbstractLogicBinopExpr extends AbstractBinopExpr implements LogicExpr, Precedence {

	public AbstractLogicBinopExpr(final Value op1, final Value op2) {
		super(Vimp.v().newArgBox(op1), Vimp.v().newArgBox(op2));
	}

	public AbstractLogicBinopExpr(final ValueBox op1box, final ValueBox op2box) {
		super(op1box, op2box);
	}

}
