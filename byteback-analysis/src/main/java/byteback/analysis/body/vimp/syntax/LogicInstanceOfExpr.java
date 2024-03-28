package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.jimple.internal.AbstractInstanceOfExpr;

/**
 * Logic equivalent of InstanceOfExpr.
 * @see soot.jimple.internal.JInstanceOfExpr
 *
 * @author paganma
 */
public class LogicInstanceOfExpr extends AbstractInstanceOfExpr implements LogicExpr {

	public LogicInstanceOfExpr(final Value op, final Type type) {
		super(Vimp.v().newArgBox(op), type);
	}

	public LogicInstanceOfExpr(final ValueBox opBox, final Type type) {
		super(opBox, type);
	}

	@Override
	public LogicInstanceOfExpr clone() {
		return new LogicInstanceOfExpr(Vimp.v().cloneIfNecessary(getOp()), getCheckType());
	}

}
