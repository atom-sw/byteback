package byteback.analysis.transformer;

import byteback.analysis.vimp.LogicExpr;
import soot.Value;

public class LogicExpressionFolder extends ExpressionFolder {

	@Override
	public boolean sideCondition(final Value substitution) {
		return substitution instanceof LogicExpr;
	}

}
