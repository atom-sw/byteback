package sootup.core.jimple.expr;

import sootup.core.jimple.basic.Immediate;
import sootup.core.jimple.common.expr.Expr;
import sootup.core.types.PrimitiveType.BooleanType;
import sootup.core.types.Type;

public abstract class SpecExpr implements Expr, Immediate {

	@Override
	public Type getType() {
		return BooleanType.getInstance();
	}

}
