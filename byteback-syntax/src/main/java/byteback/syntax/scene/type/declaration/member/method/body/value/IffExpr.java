package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.Value;
import soot.jimple.ConditionExpr;
import soot.jimple.Jimple;
import soot.jimple.internal.AbstractIntBinopExpr;
import soot.jimple.internal.ImmediateBox;

/**
 * Logical equivalence expression.
 *
 * @author paganma
 */
public class IffExpr extends AbstractIntBinopExpr implements DefaultCaseValue, ConditionExpr {

	public IffExpr(final Value op1, final Value op2) {
		super(new ImmediateBox(op1), new ImmediateBox(op2));
	}

	@Override
	public String getSymbol() {
		return " <-> ";
	}

	@Override
	public IffExpr clone() {
		return new IffExpr(Jimple.cloneIfNecessary(getOp1()), Jimple.cloneIfNecessary(getOp2()));
	}

}
