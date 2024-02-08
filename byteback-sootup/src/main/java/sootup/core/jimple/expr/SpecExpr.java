package sootup.core.jimple.expr;

import sootup.core.jimple.basic.Immediate;
import sootup.core.jimple.common.expr.Expr;
import sootup.core.jimple.visitor.ExprVisitor;
import sootup.core.jimple.visitor.SpecExprVisitor;
import sootup.core.types.PrimitiveType.BooleanType;
import sootup.core.types.Type;

public abstract class SpecExpr implements Expr, Immediate {

	public abstract void accept(final SpecExprVisitor visitor);

	@Override
	public void accept(final ExprVisitor visitor) {
		if (visitor instanceof SpecExprVisitor specExprVisitor) {
			accept(specExprVisitor);
		} else {
			visitor.defaultCaseExpr(this);
		}
	}

	@Override
	public Type getType() {
		return BooleanType.getInstance();
	}

}
