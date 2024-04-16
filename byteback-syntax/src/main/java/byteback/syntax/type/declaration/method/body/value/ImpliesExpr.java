package byteback.syntax.type.declaration.method.body.value;

import byteback.syntax.type.declaration.method.body.value.box.ConditionExprBox;
import soot.Value;
import soot.jimple.ConditionExpr;
import soot.jimple.Jimple;
import soot.jimple.internal.AbstractIntBinopExpr;

/**
 * Boolean implication expression.
 *
 * @author paganma
 */
public class ImpliesExpr extends AbstractIntBinopExpr implements DefaultCaseValue, ConditionExpr {

	public ImpliesExpr(final Value op1, final Value op2) {
		super(new ConditionExprBox(op1), new ConditionExprBox(op2));
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
