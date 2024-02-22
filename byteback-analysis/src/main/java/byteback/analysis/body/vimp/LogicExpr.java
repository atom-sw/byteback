package byteback.analysis.body.vimp;

import soot.BooleanType;
import soot.jimple.Expr;

public interface LogicExpr extends Expr {

	@Override
	default BooleanType getType() {
		return BooleanType.v();
	}

}
