package byteback.converter.soottoboogie.method.function;

import byteback.analysis.JimpleStmtSwitch;
import byteback.converter.soottoboogie.LocalExtractor;
import byteback.converter.soottoboogie.expression.PureExpressionExtractor;
import byteback.converter.soottoboogie.method.StatementConversionException;
import byteback.frontend.boogie.ast.Expression;
import byteback.frontend.boogie.ast.ValueReference;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.*;

public class FunctionBodyExtractor extends JimpleStmtSwitch<Expression> {

	private Expression result;

	@Override
	public void caseIdentityStmt(final IdentityStmt identity) {
	}

	@Override
	public void caseReturnStmt(final ReturnStmt returnStatement) {
		final Value operand = returnStatement.getOp();
		result = new FunctionExpressionExtractor().visit(operand);
	}

	@Override
	public void caseDefault(final Unit unit) {
		throw new StatementConversionException(unit, "Unable to convert statement " + unit);
	}

	@Override
	public Expression result() {
		return result;
	}

}
