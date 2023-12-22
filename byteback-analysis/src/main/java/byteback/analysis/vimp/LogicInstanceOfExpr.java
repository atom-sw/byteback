package byteback.analysis.vimp;

import byteback.analysis.Vimp;
import soot.BooleanType;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.jimple.internal.AbstractInstanceOfExpr;

public class LogicInstanceOfExpr extends AbstractInstanceOfExpr {

	public LogicInstanceOfExpr(final Value op, final Type type) {
		super(Vimp.v().newArgBox(op), type);
	}

	public LogicInstanceOfExpr(final ValueBox opbox, final Type type) {
		super(opbox, type);
	}

	@Override
	public LogicInstanceOfExpr clone() {
		return new LogicInstanceOfExpr(Vimp.cloneIfNecessary(getOp()), getCheckType());
	}

	@Override
	public Type getType() {
		return BooleanType.v();
	}

}
