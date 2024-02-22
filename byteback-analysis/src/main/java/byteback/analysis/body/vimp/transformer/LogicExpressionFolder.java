package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.vimp.LogicExpr;
import soot.Value;

public class LogicExpressionFolder extends ExpressionFolder {

	@Override
	public boolean sideCondition(final Value substitution) {
		return substitution instanceof LogicExpr;
	}

}
